package com.test.util.stream;


import com.test.lang.Iterable;
import com.test.util.*;
import com.test.util.function.*;

import java.util.Iterator;
import java.util.Objects;

/**
 * 有序的元素集合，可以添加元素，但不能删除
 * */
class SpinedBuffer<E>
        extends AbstractSpinedBuffer
        implements Consumer<E>, Iterable<E> {

    /** 目前正在写入的块，可能会也可能不会混淆第一个spine元素 */
    protected E[] curChunk;

    /** 存储所有的块，如果只有一个块则为空 */
    protected E[][] spine;

    @SuppressWarnings("unchecked")
    SpinedBuffer(int initialCapacity){
        super(initialCapacity);
        curChunk = (E[])new Object[1 << initialChunkPower];
    }

    @SuppressWarnings("unchecked")
    SpinedBuffer(){
        super();
        curChunk = (E[])new Object[1 << initialChunkPower];
    }

    /** 返回缓冲池当前的容量 */
    protected long capacity(){
        return (spineIndex == 0)
                ? curChunk.length
                : priorElementCount[spineIndex] + spine[spineIndex].length;
    }

    @SuppressWarnings("unchecked")
    private void inflateSpine(){
        if(spine == null){
            spine = (E[][])new Object[MIN_SPINE_SIZE][];
            priorElementCount = new long[MIN_SPINE_SIZE];
            spine[0] = curChunk;
        }
    }

    /** 确保缓冲区至少具有足够的大小的容量去容纳目标大小 */
    @SuppressWarnings("unchecked")
    protected final void ensureCapacity(long targetSize){
        //得到当前缓冲池的容量
        long capacity = capacity();
        //如果targetSize大于当前容量
        if(targetSize > capacity){
            //如果spine为空，初始化spine
            inflateSpine();
            for(int i=spineIndex+1;targetSize > capacity;i++){
                //如果i大于spine.length
                if(i > spine.length){
                    //把spine，priorElementCount的容量扩充为原来的两倍
                    int newSpineSize = spine.length * 2;
                    spine = Arrays.copyOf(spine,newSpineSize);
                    priorElementCount = Arrays.copyOf(priorElementCount, newSpineSize);
                }
                //获取下个块应当具有的容量
                int nextChunkSize = chunkSize(i);
                //在spine添加下个块
                spine[i] = (E[])new Object[nextChunkSize];
                //更新priorElementCount
                priorElementCount[i] = priorElementCount[i-1] + spine[i-1].length;
                capacity += nextChunkSize;
            }
        }
    }

    /** 增加容量 */
    protected void increaseCapacity(){ensureCapacity(capacity()+1);}

    /** 返回指定索引的元素 */
    public E get(long index){
        //如果spineIndex指向的是第一行的数组，则直接返回curChunk对应位置的元素
        if(spineIndex == 0){
            //如果index小于即将要写入的下一个元素的索引，直接返回，否则抛出异常
            if(index < elementIndex)
                return curChunk[(int)index];
            else
                throw new IndexOutOfBoundsException(Long.toString(index));
        }

        //如果index大于缓存池中的元素总数量，抛出异常
        if(index > count())
            throw new IndexOutOfBoundsException(Long.toString(index));

        for(int j=0;j<spineIndex;j++)
            // 当index < 索引j之前元素的总和加上spine数组的第j行的长度，表示查找到index元素所在的行
            if(index < priorElementCount[j] + spine[j].length)
                return spine[j][((int)(index - priorElementCount[j]))];

        throw new IndexOutOfBoundsException(Long.toString(index));
    }

    /** 把缓冲池的元素从offset开始复制进array */
    public void copyInto(E[] array,int offset){
        long finalOffset = offset + count();
        //如果array的长度不足以存储缓冲池的元素，抛出异常
        if(finalOffset > array.length || finalOffset < offset)
            throw new IndexOutOfBoundsException("does not fit");

        //如果curChunk在数组的第一行
        if(spineIndex == 0)
            System.arraycopy(curChunk,0,array,offset,elementIndex);
        else{
            //循环把数组当中的元素复制进入array
            for(int i=0;i<spineIndex;i++){
                System.arraycopy(spine[i],0,array,offset,spine[i].length);
                offset += spine[i].length;
            }
            //复制curChunk到数组array当中
            if(elementIndex > 0)
                System.arraycopy(curChunk,0,array,offset,elementIndex);
        }
    }

    /** 把缓冲池中的数据以数组形式返回 */
    public E[] asArray(IntFunction<E[]> arrayFactory){
        Objects.requireNonNull(arrayFactory);
        long size = count();
        if(size > Nodes.MAX_ARRAY_SIZE)
            throw new IllegalStateException(Nodes.BAD_SIZE);
        E[] array = arrayFactory.apply((int)size);
        copyInto(array,array.length);
        return array;
    }

    /** 清空缓存池 */
    @Override
    public void clear() {
        if(spine != null){
            curChunk = spine[0];
            for(int i=0;i<curChunk.length;i++)
                curChunk[i] = null;
            spine = null;
            priorElementCount = null;
        }
        else{
            for(int i=0;i<elementIndex;i++)
                curChunk[i] = null;
        }
        elementIndex = 0;
        spineIndex = 0;
    }

    /** 返回一个缓冲池的分区迭代器 */
    @Override
    public Iterator<E> iterator() {
        return Spliterators.iterator(getSpliterator());
    }

    /** 循环缓冲池里面的元素 */
    @Override
    public void forEach(Consumer<? super E> consumer){
        Objects.requireNonNull(consumer);
        //循环之前的块
        for (int j = 0; j < spineIndex; j++)
            for (E t : spine[j])
                consumer.accept(t);

        //循环正在写入的块
        for(int i=0;i<elementIndex;i++)
            consumer.accept(curChunk[i]);
    }

    @Override
    public String toString() {
        List<E> list = new ArrayList<>();
        forEach(new Consumer<E>() {
            @Override
            public void accept(E e) {
                list.add(e);
            }
        });
        return "SpinedBuffer:" + list.toString();
    }

    private static final int SPLITERATOR_CHARACTERISTICS
            = Spliterator.SIZED | Spliterator.ORDERED | Spliterator.SUBSIZED;

    public Spliterator<E> getSpliterator(){
        /**
         * 表示一个 SpineBuffer 缓冲池的分区迭代器
         *
         * 因为 SpineBuffer 是用一个二维数组 spine 来存储所有的元素，
         * 所以在 Splitr 当中如下元素的代表意义如下
         * splSpineIndex 表示二维数组中的起始位置
         * lastSpineIndex 表示二维数组的结束位置
         * splElementIndex 表示当前所在的块（即数组）的索引位置
         * lastSpineElementFence 表示最后一个块的最后一个元素的索引+1
         *
         * 举例：
         * int[][] spine = new int[][]{
         *         {1,2},
         *         {3,4,5},
         *         {6,7},
         *         {8,9,10,11},
         *         {12,13}
         * };
         * 对 spine 的第2行到第4行进行分区迭代（即{3,4,5},{6,7},{8,9,10,11}），那么如上参数代表：
         * splSpineIndex = 1; lastSpineIndex = 3; splElementIndex = 0; lastSpineElementFence = 5;
         * */
        class Splitr implements Spliterator<E>{
            //开始分区的spine数组的索引
            int splSpineIndex;

            //分区的终点spine索引
            final int lastSpineIndex;

            //最后一个spine索引的分区开始位置
            int splElementIndex;

            //最后一个spine的最后一个元素的索引+1
            final int lastSpineElementFence;

            //当前的spine数组
            E[] splChunk;

            Splitr(int firstSpineIndex,int lastSpineIndex,
                   int firstSpineElementIndex,int lastSpineElementFence){
                this.splSpineIndex = firstSpineIndex;
                this.lastSpineIndex = lastSpineIndex;
                this.splElementIndex = firstSpineElementIndex;
                this.lastSpineElementFence = lastSpineElementFence;
                assert spine != null || splSpineIndex == 0 || lastSpineIndex == 0;
                splChunk = (spine == null) ? curChunk : spine[firstSpineIndex];
            }

            @Override
            public boolean tryAdvance(Consumer<? super E> consumer) {
                Objects.requireNonNull(consumer);
                // 如果 splSpineIndex < lastSpineIndex
                // 或者当 splSpineIndex == lastSpineIndex 的时候必须有 splElementIndex < lastSpineElementFence
                if(splSpineIndex < lastSpineIndex ||
                        (splSpineIndex == lastSpineIndex && splElementIndex < lastSpineElementFence)){
                    consumer.accept(splChunk[splElementIndex++]);
                    //当一个块中的元素被消费完了，前往下一个块
                    if(splElementIndex == splChunk.length){
                        //重新初始化splElementIndex
                        splElementIndex = 0;
                        //分区迭代器的起始位置后进一位
                        ++splSpineIndex;
                        if(spine != null && splSpineIndex <= lastSpineIndex)
                            splChunk = spine[splSpineIndex];
                    }
                    return true;
                }
                return false;
            }

            @Override
            public void forEachRemaining(Consumer<? super E> consumer){
                Objects.requireNonNull(consumer);
                // 如果 splSpineIndex < lastSpineIndex
                // 或者当 splSpineIndex == lastSpineIndex 的时候必须有 splElementIndex < lastSpineElementFence
                if(splSpineIndex < lastSpineIndex ||
                        (splSpineIndex == lastSpineIndex && splElementIndex < lastSpineElementFence)){
                    //从 splElementIndex 开始循环
                    int i = splElementIndex;
                    for(int sp=splSpineIndex;sp<lastSpineIndex;sp++){
                        //创建一个chunk存储当前块
                        E[] chunk = spine[sp];
                        //循环读取当前块当中内容
                        for(;i < chunk.length;i++){
                            consumer.accept(chunk[i]);
                        }
                        i = 0;
                    }
                    //最后一个块的消费，因为最后一个块是消费到lastSpineElementFence
                    E[] chunk = (splSpineIndex == lastSpineIndex) ? splChunk : spine[lastSpineIndex];
                    int hElementIndex = lastSpineElementFence;
                    for(;i<hElementIndex;i++){
                        consumer.accept(chunk[i]);
                    }
                    splSpineIndex = lastSpineIndex;
                    splElementIndex = lastSpineElementFence;
                }
            }

            @Override
            public Spliterator<E> trySplit() {
                //如果开始分区的索引小于分区的最后一个索引
                if(splSpineIndex < lastSpineIndex){
                    //创建一个新的Splitr，开始位置为原来的位置，但是分区最后一个索引前进一位
                    Spliterator<E> ret = new Splitr(splSpineIndex,lastSpineIndex-1,
                                                    splElementIndex,spine[lastSpineIndex-1].length);
                    //因为被分出去一部分，所以splSpineIndex指向原来的末尾
                    splSpineIndex = lastSpineIndex;
                    splElementIndex = 0;
                    splChunk = spine[splSpineIndex];
                    return ret;
                }
                //如果开始索引等于结束索引，那么把splChunk分出去一半
                else if(splSpineIndex == lastSpineIndex){
                    //得到原来元素的一半的值
                    int t = (lastSpineElementFence - lastSpineIndex) / 2;
                    if(t == 0)
                        return null;
                    else{
                        //返回开始splChunk数组当中的 splElementIndex 到 splSpineIndex+t 的元素
                        Spliterator<E> ret = Arrays.spliterator(splChunk,splElementIndex,splSpineIndex + t);
                        splElementIndex += t;
                        return ret;
                    }
                }else{return null;}
            }

            @Override
            public long estimateSize() {
                // 如果只有一个块，即Splitr只有二维数组spine的一行，
                // 直接返回最后一个元素索引(lastSpineElementFence)减去第一个元素索引(splElementIndex)
                // 否则返回 splSpineIndex 至 lastSpineIndex块中的所有数量，最后加上最后一行的数量
                return (splSpineIndex == lastSpineIndex)
                        ? (long)lastSpineElementFence - splElementIndex
                        : priorElementCount[lastSpineIndex] + lastSpineElementFence -
                          priorElementCount[splSpineIndex] - splElementIndex;
            }

            @Override
            public int characteristics() {return SPLITERATOR_CHARACTERISTICS;}
        }
        return new Splitr(0,spine.length,0,elementIndex);
    }

    /** 接受一个元素进入缓冲池 */
    @Override
    public void accept(E e) {
        //如果下一个写入元素的索引等于curChunk的长度
        if(elementIndex == curChunk.length){
            inflateSpine();//如果spine为空，初始化spine和priorElementCount
            if(spineIndex+1 >= spine.length || spine[spineIndex+1] == null)
                increaseCapacity(); //扩充容量
            elementIndex = 0;
            ++spineIndex;
            curChunk = spine[spineIndex];
        }
        curChunk[elementIndex++] = e;//把元素e加入缓冲池，并且elementIndex指向下一个写入的索引
    }

    /** 一个表示接受包装类型的抽象缓冲池 */
    abstract static class OfPrimitive<E,T_ARR,T_CONS> extends AbstractSpinedBuffer implements Iterable<E>{
        T_ARR curChunk;

        T_ARR[] spine;

        OfPrimitive(int initialCapacity){
            super(initialCapacity);
            curChunk = newArray(1 << initialChunkPower);
        }

        OfPrimitive() {
            super();
            curChunk = newArray(1 << initialChunkPower);
        }

        @Override
        public abstract Iterator<E> iterator();

        @Override
        public abstract void forEach(Consumer<? super E> consumer);

        /** 创建一个正确类型和大小的新二维数组 */
        protected abstract T_ARR[] newArrayArray(int size);

        /** 创建一个正确类型和大小的新数组 */
        public abstract T_ARR newArray(int size);

        /** 返回数组array的长度 */
        protected abstract int arrayLength(T_ARR array);

        /** 通过提供的consumer循环消费array中的from到to的元素 */
        protected abstract void arrayForEach(T_ARR array, int from, int to,
                                             T_CONS consumer);

        /** 返回缓冲池当前的容量 */
        protected long capacity() {
            return (spineIndex == 0)
                    ? arrayLength(curChunk)
                    : priorElementCount[spineIndex] + arrayLength(spine[spineIndex]);
        }

        /** 如果spine为null，初始化spine，priorElementCount */
        private void inflateSpine() {
            if (spine == null) {
                spine = newArrayArray(MIN_SPINE_SIZE);
                priorElementCount = new long[MIN_SPINE_SIZE];
                spine[0] = curChunk;
            }
        }

        /** 确保缓冲区至少具有足够的大小的容量去容纳目标大小 */
        protected final void ensureCapacity(long targetSize) {
            long capacity = capacity();
            if (targetSize > capacity) {
                inflateSpine();
                for (int i=spineIndex+1; targetSize > capacity; i++) {
                    if (i >= spine.length) {
                        int newSpineSize = spine.length * 2;
                        spine = Arrays.copyOf(spine, newSpineSize);
                        priorElementCount = Arrays.copyOf(priorElementCount, newSpineSize);
                    }
                    int nextChunkSize = chunkSize(i);
                    spine[i] = newArray(nextChunkSize);
                    priorElementCount[i] = priorElementCount[i-1] + arrayLength(spine[i - 1]);
                    capacity += nextChunkSize;
                }
            }
        }

        /** 增加容量 */
        protected void increaseCapacity() {
            ensureCapacity(capacity() + 1);
        }

        /** 返回索引index所在的块 */
        protected int chunkFor(long index){
            if(spineIndex == 0){
                if(index < elementIndex)
                    return 0;
                else
                    throw new IndexOutOfBoundsException(Long.toString(index));
            }

            if(index > count())
                throw new IndexOutOfBoundsException(Long.toString(index));

            for(int j=0;j <= spineIndex;j++)
                if(index < priorElementCount[j] + arrayLength(spine[j]))
                    return j;

            throw new IndexOutOfBoundsException(Long.toString(index));
        }

        /** 把缓冲池的元素从offset开始复制进array */
        public void copyInto(T_ARR array, int offset){
            long finalOffset = offset + count();
            if (finalOffset > arrayLength(array) || finalOffset < offset) {
                throw new IndexOutOfBoundsException("does not fit");
            }

            if (spineIndex == 0)
                System.arraycopy(curChunk, 0, array, offset, elementIndex);
            else {
                // full chunks
                for (int i=0; i < spineIndex; i++) {
                    System.arraycopy(spine[i], 0, array, offset, arrayLength(spine[i]));
                    offset += arrayLength(spine[i]);
                }
                if (elementIndex > 0)
                    System.arraycopy(curChunk, 0, array, offset, elementIndex);
            }
        }

        /** 把缓冲池中的数据以数组形式返回 */
        public T_ARR asPrimitiveArray(){
            long size = count();
            if(size > Nodes.MAX_ARRAY_SIZE)
                throw new IllegalStateException();
            T_ARR result = newArray((int)size);
            copyInto(result,arrayLength(result));
            return result;
        }

        /** 接受一个参数之前的处理 */
        protected void preAccept(){
            if (elementIndex == arrayLength(curChunk)) {
                inflateSpine();
                if (spineIndex+1 >= spine.length || spine[spineIndex+1] == null)
                    increaseCapacity();
                elementIndex = 0;
                ++spineIndex;
                curChunk = spine[spineIndex];
            }
        }

        /** 清空缓存池 */
        public void clear() {
            if (spine != null) {
                curChunk = spine[0];
                spine = null;
                priorElementCount = null;
            }
            elementIndex = 0;
            spineIndex = 0;
        }

        @SuppressWarnings("overloads")
        public void forEach(T_CONS consumer) {
            // completed chunks, if any
            for (int j = 0; j < spineIndex; j++)
                arrayForEach(spine[j], 0, arrayLength(spine[j]), consumer);

            // current chunk
            arrayForEach(curChunk, 0, elementIndex, consumer);
        }

        /** 一个用于 SpinedBuffer.OfPrimitive 的基础分区迭代器 */
        abstract class BaseSpliterator<T_SPLITR extends Spliterator.OfPrimitive<E,T_CONS,T_SPLITR>>
                implements Spliterator.OfPrimitive<E,T_CONS,T_SPLITR>{
            //开始分区的spine数组的索引
            int splSpineIndex;

            //分区的终点spine索引
            final int lastSpineIndex;

            //最后一个spine索引的分区开始位置
            int splElementIndex;

            //最后一个spine的最后一个元素的索引+1
            final int lastSpineElementFence;

            //当前的spine数组
            T_ARR splChunk;

            BaseSpliterator(int firstSpineIndex, int lastSpineIndex,
                            int firstSpineElementIndex, int lastSpineElementFence){
                this.splSpineIndex = firstSpineIndex;
                this.lastSpineIndex = lastSpineIndex;
                this.splElementIndex = firstSpineElementIndex;
                this.lastSpineElementFence = lastSpineElementFence;
                assert spine != null || firstSpineIndex == 0 && lastSpineIndex == 0;
                splChunk = (spine == null) ? curChunk : spine[firstSpineIndex];
            }

            /** 返回一个 BaseSpliterator*/
            abstract T_SPLITR newSpliterator(int firstSpineIndex, int lastSpineIndex,
                                             int firstSpineElementIndex, int lastSpineElementFence);

            /** 从索引index开始循环array */
            abstract void arrayForOne(T_ARR array, int index, T_CONS consumer);

            /** 对于array，根据offset和len返回一个 BaseSpliterator */
            abstract T_SPLITR arraySpliterator(T_ARR array, int offset, int len);

            @Override
            public long estimateSize() {
                // 如果只有一个块，即Splitr只有二维数组spine的一行，
                // 直接返回最后一个元素索引(lastSpineElementFence)减去第一个元素索引(splElementIndex)
                // 否则返回 splSpineIndex 至 lastSpineIndex块中的所有数量，最后加上最后一行的数量
                return (splSpineIndex == lastSpineIndex)
                        ? (long) lastSpineElementFence - splElementIndex
                        : // # of elements prior to end -
                        priorElementCount[lastSpineIndex] + lastSpineElementFence -
                                // # of elements prior to current
                                priorElementCount[splSpineIndex] - splElementIndex;
            }

            @Override
            public int characteristics() {
                return SPLITERATOR_CHARACTERISTICS;
            }

            @Override
            public boolean tryAdvance(T_CONS consumer) {
                Objects.requireNonNull(consumer);
                // 如果 splSpineIndex < lastSpineIndex
                // 或者当 splSpineIndex == lastSpineIndex 的时候必须有 splElementIndex < lastSpineElementFence
                if (splSpineIndex < lastSpineIndex
                        || (splSpineIndex == lastSpineIndex && splElementIndex < lastSpineElementFence)) {
                    arrayForOne(splChunk, splElementIndex++, consumer);
                    //当一个块中的元素被消费完了，前往下一个块
                    if (splElementIndex == arrayLength(splChunk)) {
                        //重新初始化splElementIndex
                        splElementIndex = 0;
                        //分区迭代器的起始位置后进一位
                        ++splSpineIndex;
                        if (spine != null && splSpineIndex <= lastSpineIndex)
                            splChunk = spine[splSpineIndex];
                    }
                    return true;
                }
                return false;
            }

            @Override
            public void forEachRemaining(T_CONS consumer) {
                Objects.requireNonNull(consumer);
                // 如果 splSpineIndex < lastSpineIndex
                // 或者当 splSpineIndex == lastSpineIndex 的时候必须有 splElementIndex < lastSpineElementFence
                if (splSpineIndex < lastSpineIndex
                        || (splSpineIndex == lastSpineIndex && splElementIndex < lastSpineElementFence)) {
                    //从 splElementIndex 开始循环
                    int i = splElementIndex;
                    // completed chunks, if any
                    for (int sp = splSpineIndex; sp < lastSpineIndex; sp++) {
                        //创建一个chunk存储当前块
                        T_ARR chunk = spine[sp];
                        //循环读取当前块当中内容
                        arrayForEach(chunk, i, arrayLength(chunk), consumer);
                        i = 0;
                    }
                    //最后一个块的消费，因为最后一个块是消费到lastSpineElementFence
                    T_ARR chunk = (splSpineIndex == lastSpineIndex) ? splChunk : spine[lastSpineIndex];
                    arrayForEach(chunk, i, lastSpineElementFence, consumer);
                    // mark consumed
                    splSpineIndex = lastSpineIndex;
                    splElementIndex = lastSpineElementFence;
                }
            }

            @Override
            public T_SPLITR trySplit() {
                //如果开始分区的索引小于分区的最后一个索引
                if (splSpineIndex < lastSpineIndex) {
                    //创建一个新的Splitr，开始位置为原来的位置，但是分区最后一个索引前进一位
                    T_SPLITR ret = newSpliterator(splSpineIndex, lastSpineIndex - 1,
                            splElementIndex, arrayLength(spine[lastSpineIndex - 1]));
                    //因为被分出去一部分，所以splSpineIndex指向原来的末尾
                    splSpineIndex = lastSpineIndex;
                    splElementIndex = 0;
                    splChunk = spine[splSpineIndex];
                    return ret;
                }
                else if (splSpineIndex == lastSpineIndex) {
                    //如果开始索引等于结束索引，那么把splChunk分出去一半
                    int t = (lastSpineElementFence - splElementIndex) / 2;
                    if (t == 0)
                        return null;
                    else {
                        //返回开始splChunk数组当中的 splElementIndex 到 splSpineIndex+t 的元素
                        T_SPLITR ret = arraySpliterator(splChunk, splElementIndex, t);
                        splElementIndex += t;
                        return ret;
                    }
                }
                else {
                    return null;
                }
            }
        }
    }

    /** 一个表示接受Integer类型的抽象缓冲池 */
    static class OfInt extends SpinedBuffer.OfPrimitive<Integer,int[], IntConsumer>
            implements IntConsumer{
        OfInt(){}

        OfInt(int initialCapacity) {
            super(initialCapacity);
        }

        @Override
        public void forEach(Consumer<? super Integer> consumer) {
            if(consumer instanceof IntConsumer)
                forEach((IntConsumer)consumer);
            else{
                if(Tripwire.ENABLED)
                    Tripwire.trip(getClass(),"{0} calling SpinedBuffer.OfInt.forEach(Consumer)");
                getSpliterator().forEachRemaining(consumer);
            }
        }

        @Override
        protected int[][] newArrayArray(int size) {return new int[size][];}

        @Override
        public int[] newArray(int size) {return new int[size];}

        @Override
        protected int arrayLength(int[] array) {return array.length;}

        @Override
        protected void arrayForEach(int[] array, int from, int to, IntConsumer consumer) {
            for (int i = from; i < to; i++)
                consumer.accept(array[i]);
        }

        @Override
        public void accept(int value) {
            preAccept();
            curChunk[elementIndex++] = value;
        }

        public int get(long index){
            int ch = chunkFor(index);
            if(spineIndex == 0 && ch == 0)
                return curChunk[(int)index];
            else
                return spine[ch][(int)(index -  priorElementCount[ch])];
        }

        @Override
        public Iterator<Integer> iterator() {
            return Spliterators.iterator(getSpliterator());
        }

        public Spliterator.OfInt getSpliterator(){
            class Splitr extends BaseSpliterator<Spliterator.OfInt>
                    implements Spliterator.OfInt{
                Splitr(int firstSpineIndex, int lastSpineIndex, int firstSpineElementIndex, int lastSpineElementFence) {
                    super(firstSpineIndex, lastSpineIndex, firstSpineElementIndex, lastSpineElementFence);
                }

                @Override
                OfInt newSpliterator(int firstSpineIndex, int lastSpineIndex, int firstSpineElementIndex, int lastSpineElementFence) {
                    return new Splitr(firstSpineIndex, lastSpineIndex,
                            firstSpineElementIndex, lastSpineElementFence);
                }

                @Override
                void arrayForOne(int[] array, int index, IntConsumer consumer) {
                    consumer.accept(array[index]);
                }

                @Override
                OfInt arraySpliterator(int[] array, int offset, int len) {
                    return Arrays.spliterator(array, offset, offset+len);
                }
            }
            return new Splitr(0, spineIndex, 0, elementIndex);
        }

        @Override
        public String toString() {
            int[] array = asPrimitiveArray();
            if (array.length < 200) {
                return String.format("%s[length=%d, chunks=%d]%s",
                        getClass().getSimpleName(), array.length,
                        spineIndex, Arrays.toString(array));
            }
            else {
                int[] array2 = Arrays.copyOf(array, 200);
                return String.format("%s[length=%d, chunks=%d]%s...",
                        getClass().getSimpleName(), array.length,
                        spineIndex, Arrays.toString(array2));
            }
        }
    }

    /** 一个表示接受Long类型的抽象缓冲池 */
    static class OfLong extends SpinedBuffer.OfPrimitive<Long,long[], LongConsumer>
            implements LongConsumer {
        OfLong(){}

        OfLong(int initialCapacity) {
            super(initialCapacity);
        }

        @Override
        public void forEach(Consumer<? super Long> consumer) {
            if(consumer instanceof LongConsumer)
                forEach((LongConsumer)consumer);
            else{
                if(Tripwire.ENABLED)
                    Tripwire.trip(getClass(),"{0} calling SpinedBuffer.OfLong.forEach(Consumer)");
                getSpliterator().forEachRemaining(consumer);
            }
        }

        @Override
        protected long[][] newArrayArray(int size) {return new long[size][];}

        @Override
        public long[] newArray(int size) {return new long[size];}

        @Override
        protected int arrayLength(long[] array) {return array.length;}

        @Override
        protected void arrayForEach(long[] array, int from, int to, LongConsumer consumer) {
            for (int i = from; i < to; i++)
                consumer.accept(array[i]);
        }

        @Override
        public void accept(long value) {
            preAccept();
            curChunk[elementIndex++] = value;
        }

        public long get(long index){
            int ch = chunkFor(index);
            if(spineIndex == 0 && ch == 0)
                return curChunk[(int)index];
            else
                return spine[ch][(int)(index -  priorElementCount[ch])];
        }

        @Override
        public Iterator<Long> iterator() {
            return Spliterators.iterator(getSpliterator());
        }

        public Spliterator.OfLong getSpliterator(){
            class Splitr extends BaseSpliterator<Spliterator.OfLong>
                    implements Spliterator.OfLong{
                Splitr(int firstSpineIndex, int lastSpineIndex, int firstSpineElementIndex, int lastSpineElementFence) {
                    super(firstSpineIndex, lastSpineIndex, firstSpineElementIndex, lastSpineElementFence);
                }

                @Override
                OfLong newSpliterator(int firstSpineIndex, int lastSpineIndex, int firstSpineElementIndex, int lastSpineElementFence) {
                    return new Splitr(firstSpineIndex, lastSpineIndex,
                            firstSpineElementIndex, lastSpineElementFence);
                }

                @Override
                void arrayForOne(long[] array, int index, LongConsumer consumer) {
                    consumer.accept(array[index]);
                }

                @Override
                OfLong arraySpliterator(long[] array, int offset, int len) {
                    return Arrays.spliterator(array, offset, offset+len);
                }
            }
            return new Splitr(0, spineIndex, 0, elementIndex);
        }

        @Override
        public String toString() {
            long[] array = asPrimitiveArray();
            if (array.length < 200) {
                return String.format("%s[length=%d, chunks=%d]%s",
                        getClass().getSimpleName(), array.length,
                        spineIndex, Arrays.toString(array));
            }
            else {
                long[] array2 = Arrays.copyOf(array, 200);
                return String.format("%s[length=%d, chunks=%d]%s...",
                        getClass().getSimpleName(), array.length,
                        spineIndex, Arrays.toString(array2));
            }
        }
    }

    /** 一个表示接受Double类型的抽象缓冲池 */
    static class OfDouble extends SpinedBuffer.OfPrimitive<Double,double[], DoubleConsumer>
            implements DoubleConsumer {
        OfDouble(){}

        OfDouble(int initialCapacity) {
            super(initialCapacity);
        }

        @Override
        public void forEach(Consumer<? super Double> consumer) {
            if(consumer instanceof DoubleConsumer)
                forEach((DoubleConsumer)consumer);
            else{
                if(Tripwire.ENABLED)
                    Tripwire.trip(getClass(),"{0} calling SpinedBuffer.OfDouble.forEach(Consumer)");
                getSpliterator().forEachRemaining(consumer);
            }
        }

        @Override
        protected double[][] newArrayArray(int size) {return new double[size][];}

        @Override
        public double[] newArray(int size) {return new double[size];}

        @Override
        protected int arrayLength(double[] array) {return array.length;}

        @Override
        protected void arrayForEach(double[] array, int from, int to, DoubleConsumer consumer) {
            for (int i = from; i < to; i++)
                consumer.accept(array[i]);
        }

        @Override
        public void accept(double value) {
            preAccept();
            curChunk[elementIndex++] = value;
        }

        public double get(long index){
            int ch = chunkFor(index);
            if(spineIndex == 0 && ch == 0)
                return curChunk[(int)index];
            else
                return spine[ch][(int)(index -  priorElementCount[ch])];
        }

        @Override
        public Iterator<Double> iterator() {
            return Spliterators.iterator(getSpliterator());
        }

        public Spliterator.OfDouble getSpliterator(){
            class Splitr extends BaseSpliterator<Spliterator.OfDouble>
                    implements Spliterator.OfDouble{
                Splitr(int firstSpineIndex, int lastSpineIndex, int firstSpineElementIndex, int lastSpineElementFence) {
                    super(firstSpineIndex, lastSpineIndex, firstSpineElementIndex, lastSpineElementFence);
                }

                @Override
                OfDouble newSpliterator(int firstSpineIndex, int lastSpineIndex, int firstSpineElementIndex, int lastSpineElementFence) {
                    return new Splitr(firstSpineIndex, lastSpineIndex,
                            firstSpineElementIndex, lastSpineElementFence);
                }

                @Override
                void arrayForOne(double[] array, int index, DoubleConsumer consumer) {
                    consumer.accept(array[index]);
                }

                @Override
                OfDouble arraySpliterator(double[] array, int offset, int len) {
                    return Arrays.spliterator(array, offset, offset+len);
                }
            }
            return new Splitr(0, spineIndex, 0, elementIndex);
        }

        @Override
        public String toString() {
            double[] array = asPrimitiveArray();
            if (array.length < 200) {
                return String.format("%s[length=%d, chunks=%d]%s",
                        getClass().getSimpleName(), array.length,
                        spineIndex, Arrays.toString(array));
            }
            else {
                double[] array2 = Arrays.copyOf(array, 200);
                return String.format("%s[length=%d, chunks=%d]%s...",
                        getClass().getSimpleName(), array.length,
                        spineIndex, Arrays.toString(array2));
            }
        }
    }
}
