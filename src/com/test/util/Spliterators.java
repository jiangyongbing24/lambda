package com.test.util;

import com.test.lang.IllegalStateException;
import com.test.util.function.Consumer;
import com.test.util.function.DoubleConsumer;
import com.test.util.function.IntConsumer;
import com.test.util.function.LongConsumer;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * @Created by JYB
 * @Date 2019/7/15 20:03
 * @Description 提供各种分区迭代器
 */
public final class Spliterators {
    private Spliterators(){}

    /**
     * 返回一个空分区迭代器
     * */
    public static <T> Spliterator<T> emptySpliterator(){return (Spliterator<T>) EMPTY_SPLITERATOR;}

    private static final Spliterator<Object> EMPTY_SPLITERATOR =
            new EmptySpliterator.OfRef<>();

    /**
     * 返回INTEGER类型的空分区迭代器
     * */
    public static Spliterator.OfInt emptyIntSpliterator(){return (Spliterator.OfInt) EMPTY_INT_SPLITERATOR;}

    private static final Spliterator.OfInt EMPTY_INT_SPLITERATOR =
            new EmptySpliterator.OfInt();

    /**
     * 返回LONG类型的空分区迭代器
     * */
    private static Spliterator.OfLong emptyLongSpliterator(){return (Spliterator.OfLong) EMPTY_LONG_SPLITERATOR;}
    private static final Spliterator.OfLong EMPTY_LONG_SPLITERATOR =
            new EmptySpliterator.OfLong();

    /**
     * 返回DOUBLE类型的空分区迭代器
     * */
    public static Spliterator.OfDouble getEmptyDoubleSpliterator(){return (Spliterator.OfDouble) EMPTY_DOUBLE_SPLITERATOR;}

    private static final Spliterator.OfDouble EMPTY_DOUBLE_SPLITERATOR =
            new EmptySpliterator.OfDouble();

    /**
     * 获取一个数组分区迭代器
     * */
    public static <T> Spliterator<T> spliterator(Object[] array,int additionalCharacteristics){
        return new ArraySpliterator(Objects.requireNonNull(array),0,array.length,additionalCharacteristics);
    }

    /**
     * 获取一个数组分区迭代器
     * */
    public static <T> Spliterator<T> spliterator(Object[] array,int fromIndex,int toIndex,int additionalCharacteristics){
        checkFromToBounds(Objects.requireNonNull(array).length,fromIndex,toIndex);
        return new ArraySpliterator(Objects.requireNonNull(array),fromIndex,toIndex,additionalCharacteristics);
    }

    /**
     * 获取一个整数数组分区迭代器
     * */
    public static Spliterator.OfInt spliterator(int[] array,int additionalCharacteristics){
        return new IntArraySpliterator(Objects.requireNonNull(array),0,array.length,additionalCharacteristics);
    }

    /**
     * 获取一个整数数组分区迭代器
     * */
    public static Spliterator.OfInt spliterator(int[] array,int fromIndex,int toIndex,int additionalCharacteristics){
        checkFromToBounds(Objects.requireNonNull(array).length,fromIndex,toIndex);
        return new IntArraySpliterator(Objects.requireNonNull(array),0,array.length,additionalCharacteristics);
    }

    /**
     * 获取一个长整数数组分区迭代器
     * */
    public static Spliterator.OfLong spliterator(long[] array,int additionalCharacteristics){
        return new LongArraySpliterator(Objects.requireNonNull(array),0,array.length,additionalCharacteristics);
    }

    /**
     * 获取一个整数数组分区迭代器
     * */
    public static Spliterator.OfLong spliterator(long[] array,int fromIndex,int toIndex,int additionalCharacteristics){
        checkFromToBounds(Objects.requireNonNull(array).length,fromIndex,toIndex);
        return new LongArraySpliterator(Objects.requireNonNull(array),0,array.length,additionalCharacteristics);
    }

    /**
     * 获取一个浮点数整数数组分区迭代器
     * */
    public static Spliterator.OfDouble spliterator(double[] array,int additionalCharacteristics){
        return new DoubleArraySpliterator(Objects.requireNonNull(array),0,array.length,additionalCharacteristics);
    }

    /**
     * 获取一个浮点数数组分区迭代器
     * */
    public static Spliterator.OfDouble spliterator(double[] array,int fromIndex,int toIndex,int additionalCharacteristics){
        checkFromToBounds(Objects.requireNonNull(array).length,fromIndex,toIndex);
        return new DoubleArraySpliterator(Objects.requireNonNull(array),0,array.length,additionalCharacteristics);
    }

    private static void checkFromToBounds(int arrayLength,int origin,int fence){
        if(origin > fence)
            throw new ArrayIndexOutOfBoundsException(
                    "origin(" + origin + ") > fence(" + fence + ")");
        if (origin < 0)
            throw new ArrayIndexOutOfBoundsException(origin);
        if(arrayLength < fence)
            throw new ArrayIndexOutOfBoundsException(fence);
    }

    /**
     * 获取一个集合分区迭代器
     * */
    public static <T> Spliterator<T> spliterator(Collection<? extends T> collection,int characteristics){
        return new IteratorSpliterator<>(Objects.requireNonNull(collection),characteristics);
    }

    /**
     * 获取一个集合分区迭代器
     * */
    public static <T> Spliterator<T> spliterator(Iterator<? extends T> iterator,long size,int characteristics){
        return new IteratorSpliterator<>(Objects.requireNonNull(iterator),size,characteristics);
    }

    /**
     * 获取一个未知集合大小的分区迭代器
     * */
    public static <T> Spliterator<T> spliteratorUnknownSize(Iterator<? extends T> iterator,int characteristics){
        return new IteratorSpliterator<T>(Objects.requireNonNull(iterator),characteristics);
    }

    /**
     * 获取一个整数集合分区迭代器
     * */
    public static Spliterator.OfInt spliterator(PrimitiveIterator.OfInt iterator,long size,int characteristics){
        return new IntIteratorSpliterator(Objects.requireNonNull(iterator),size,characteristics);
    }

    /**
     * 获取一个未知大小的整数集合分区迭代器
     * */
    public static Spliterator.OfInt spliteratorUnknownSize(PrimitiveIterator.OfInt iterator,int characteristics){
        return new IntIteratorSpliterator(Objects.requireNonNull(iterator),characteristics);
    }

    /**
     * 获取一个长整数集合分区迭代器
     * */
    public static Spliterator.OfLong spliterator(PrimitiveIterator.OfLong iterator,long size,int characteristics){
        return new LongIteratorSpliterator(Objects.requireNonNull(iterator),size,characteristics);
    }

    /**
     * 获取一个未知大小的长整数集合分区迭代器
     * */
    public static Spliterator.OfLong spliteratorUnknownSize(PrimitiveIterator.OfLong iterator,int characteristics){
        return new LongIteratorSpliterator(Objects.requireNonNull(iterator),characteristics);
    }

    /**
     * 获取一个浮点数集合分区迭代器
     * */
    public static Spliterator.OfDouble spliterator(PrimitiveIterator.OfDouble iterator,long size,int characteristics){
        return new DoubleIteratorSpliterator(Objects.requireNonNull(iterator),size,characteristics);
    }

    /**
     * 获取一个未知大小的浮点数集合分区迭代器
     * */
    public static Spliterator.OfDouble spliteratorUnknownSize(PrimitiveIterator.OfDouble iterator,int characteristics){
        return new DoubleIteratorSpliterator(Objects.requireNonNull(iterator),characteristics);
    }

    /**
     * 根据一个分区迭代器返回一个迭代器对象
     * */
    public static <T> Iterator<T> iterator(Spliterator<? extends T> spliterator){
        Objects.requireNonNull(spliterator);

        /**
         * 适配器类，把spliterator对象的方法适配成Iterator接口的方法
         * */
        class Adapter implements Iterator<T>,Consumer<T>{
            boolean valueReady = false;//用来确定tryAdvance不会一直死循环
            T nextElement;//存储当前存储的值

            //此方法巧妙的利用Spliterator.tryAdvance的特性，加上双重判断避免死循环
            //一种可能的情况就是Spliterator.tryAdvance会一直返回true
            //但是Spliterator.tryAdvance运行到最后一个的时候不会执行消费者Consumer
            //即不会重新初始化valueReady，所以即使一直返回true也不会使Iterator陷入死循环
            @Override
            public void accept(T t) {
                valueReady = true;
                nextElement = t;
            }

            @Override
            public boolean hasNext() {
                if (!valueReady)
                    spliterator.tryAdvance(this);
                return valueReady;
            }

            @Override
            public T next() {
                if (!valueReady && !hasNext())
                    throw new NoSuchElementException();
                else {
                    valueReady = false;
                    return nextElement;
                }
            }
        }

        return new Adapter();
    }

    /**
     * 根据一个整数分区迭代器返回一个整数迭代器对象
     * */
    public static PrimitiveIterator.OfInt iterator(Spliterator.OfInt spliterator){
        Objects.requireNonNull(spliterator);
        class Adapter implements PrimitiveIterator.OfInt,IntConsumer{
            boolean valueReady = false;//用来确定tryAdvance不会一直死循环
            int nextElement;//存储当前存储的值

            @Override
            public void accept(int t) {
                valueReady = true;
                nextElement = t;
            }

            @Override
            public boolean hasNext() {
                if (!valueReady)
                    spliterator.tryAdvance(this);
                return valueReady;
            }

            @Override
            public int nextInt() {
                if (!valueReady && !hasNext())
                    throw new NoSuchElementException();
                else {
                    valueReady = false;
                    return nextElement;
                }
            }
        }

        return new Adapter();
    }

    /**
     * 根据一个长整数分区迭代器返回一个长整数迭代器对象
     * */
    public static PrimitiveIterator.OfLong iterator(Spliterator.OfLong spliterator){
        Objects.requireNonNull(spliterator);
        class Adapter implements PrimitiveIterator.OfLong,LongConsumer{
            boolean valueReady = false;//用来确定tryAdvance不会一直死循环
            long nextElement;//存储当前存储的值

            @Override
            public void accept(long t) {
                valueReady = true;
                nextElement = t;
            }

            @Override
            public boolean hasNext() {
                if (!valueReady)
                    spliterator.tryAdvance(this);
                return valueReady;
            }

            @Override
            public long nextLong() {
                if (!valueReady && !hasNext())
                    throw new NoSuchElementException();
                else {
                    valueReady = false;
                    return nextElement;
                }
            }
        }

        return new Adapter();
    }

    /**
     * 根据一个浮点数分区迭代器返回一个浮点数迭代器对象
     * */
    public static PrimitiveIterator.OfDouble iterator(Spliterator.OfDouble spliterator){
        Objects.requireNonNull(spliterator);
        class Adapter implements PrimitiveIterator.OfDouble,DoubleConsumer{
            boolean valueReady = false;//用来确定tryAdvance不会一直死循环
            double nextElement;//存储当前存储的值

            @Override
            public void accept(double t) {
                valueReady = true;
                nextElement = t;
            }

            @Override
            public boolean hasNext() {
                if (!valueReady)
                    spliterator.tryAdvance(this);
                return valueReady;
            }

            @Override
            public double nextDouble() {
                if (!valueReady && !hasNext())
                    throw new NoSuchElementException();
                else {
                    valueReady = false;
                    return nextElement;
                }
            }
        }

        return new Adapter();
    }

    /**
     * 一个空分区迭代器
     * */
    private static abstract class EmptySpliterator<T,S extends Spliterator<T>,C>{
        EmptySpliterator(){}

        public S trySplit(){return null;}

        public boolean tryAdvance(C action){
            Objects.requireNonNull(action);
            return false;
        }

        public void forEachRemaining(C action){Objects.requireNonNull(action);}

        public long estimateSize(){return 0;}

        public int characteristics(){return Spliterator.SIZED | Spliterator.SUBSIZED;}

        /**
         * 表示只接受基本类型的包装类的空分区迭代器
         * */
        public static final class OfRef<T>
                extends EmptySpliterator<T,Spliterator<T>, Consumer<? super T>>
                implements Spliterator<T>{
            OfRef(){}
        }

        /**
         * 表示只接受Integer类型的的空分区迭代器
         * */
        public static final class OfInt
                extends EmptySpliterator<Integer, Spliterator.OfInt, IntConsumer>
                implements Spliterator.OfInt{
            OfInt(){}
        }

        /**
         * 表示只接受Long类型的的空分区迭代器
         * */
        public static final class OfLong
                extends EmptySpliterator<Long,Spliterator.OfLong, LongConsumer>
                implements Spliterator.OfLong{
            OfLong(){}
        }

        /**
         * 表示只接受Double类型的的空分区迭代器
         * */
        public static final class OfDouble
                extends EmptySpliterator<Double,Spliterator.OfDouble, DoubleConsumer>
                implements Spliterator.OfDouble{
            OfDouble(){}
        }
    }

    /**
     * 一个用于分割数组的分区迭代器
     * */
    static final class ArraySpliterator<T> implements Spliterator<T>{

        private final Object[] array;
        private int index;
        private final int fence;
        private final int characteristics;

        public ArraySpliterator(Object[] array,int additionalCharacteristics){
            this(array,0,array.length,additionalCharacteristics);
        }

        public ArraySpliterator(Object[] array,int origin,int fence,int additionalCharacteristics){
            this.array = array;
            this.index = origin;
            this.fence = fence;
            this.characteristics = additionalCharacteristics | Spliterator.SIZED | Spliterator.SUBSIZED;
        }

        @Override
        public boolean tryAdvance(Consumer<? super T> action) {
            Objects.requireNonNull(action);
            if(index >= 0 && index < fence){
                @SuppressWarnings("unchecked")T e = (T)array[index++];
                action.accept(e);
                return true;
            }
            return false;
        }

        @Override
        public Spliterator<T> trySplit() {
            int lo = index,mid = (fence + lo) >>> 1;
            return lo >= mid ? null : new ArraySpliterator<>(array,lo,index = mid,characteristics);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void forEachRemaining(Consumer<? super T> action){
            Objects.requireNonNull(action);
            Object[] a;int i;int hi;
            if((a = array).length >= (hi = fence)
                    && (i = index) >= 0 && i < (index = hi)){
                do{action.accept((T)a[i]);}while(++i < hi);
            }
        }

        @Override
        public long estimateSize() {
            return (long) (fence - index);
        }

        @Override
        public int characteristics() {
            return characteristics;
        }

        public Comparator<? super T> getComparator(){
            if(hasCharacteristics(Spliterator.SORTED))
                return null;
            throw new IllegalStateException();
        }
    }

    /**
     * 整数数组分区迭代器
     * */
    static final class IntArraySpliterator implements Spliterator.OfInt{

        private final int[] array;
        private int index;
        private final int fence;
        private final int characteristics;

        public IntArraySpliterator(int[] array,int additionalCharacteristics){
            this(array,0,array.length,additionalCharacteristics);
        }

        public IntArraySpliterator(int[] array,int origin,int fence,int additionalCharacteristics){
            this.array = array;
            this.index = origin;
            this.fence = fence;
            this.characteristics = additionalCharacteristics | Spliterator.SIZED | Spliterator.SUBSIZED;
        }

        @Override
        public boolean tryAdvance(IntConsumer action) {
            Objects.requireNonNull(action);
            if(index >= 0 && index < fence){
                action.accept(array[index++]);
                return true;
            }
            return false;
        }

        @Override
        public OfInt trySplit() {
            int lo = index,mid = (fence + lo) >>> 1;
            return lo >= mid ? null : new IntArraySpliterator(array,lo,index = mid,characteristics);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void forEachRemaining(IntConsumer action){
            Objects.requireNonNull(action);
            int[] a;int i;int hi;
            if((a = array).length >= (hi = fence)
                    && (i = index) >= 0 && i < (index = hi)){
                do{action.accept(a[i]);}while(++i < hi);
            }
        }

        @Override
        public long estimateSize() {
            return (long) (fence - index);
        }

        @Override
        public int characteristics() {
            return characteristics;
        }

        public Comparator<? super Integer> getComparator(){
            if(hasCharacteristics(Spliterator.SORTED))
                return null;
            throw new IllegalStateException();
        }
    }

    /**
     * 长整数数组分区迭代器
     * */
    static final class LongArraySpliterator implements Spliterator.OfLong{

        private final long[] array;
        private int index;
        private final int fence;
        private final int characteristics;

        public LongArraySpliterator(long[] array,int additionalCharacteristics){
            this(array,0,array.length,additionalCharacteristics);
        }

        public LongArraySpliterator(long[] array,int origin,int fence,int additionalCharacteristics){
            this.array = array;
            this.index = origin;
            this.fence = fence;
            this.characteristics = additionalCharacteristics | Spliterator.SIZED | Spliterator.SUBSIZED;
        }

        @Override
        public boolean tryAdvance(LongConsumer action) {
            Objects.requireNonNull(action);
            if(index >= 0 && index < fence){
                action.accept(array[index++]);
                return true;
            }
            return false;
        }

        @Override
        public OfLong trySplit() {
            int lo = index,mid = (fence + lo) >>> 1;
            return lo >= mid ? null : new LongArraySpliterator(array,lo,index = mid,characteristics);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void forEachRemaining(LongConsumer action){
            Objects.requireNonNull(action);
            long[] a;int i;int hi;
            if((a = array).length >= (hi = fence)
                    && (i = index) >= 0 && i < (index = hi)){
                do{action.accept(a[i]);}while(++i < hi);
            }
        }

        @Override
        public long estimateSize() {
            return (long) (fence - index);
        }

        @Override
        public int characteristics() {
            return characteristics;
        }

        public Comparator<? super Long> getComparator(){
            if(hasCharacteristics(Spliterator.SORTED))
                return null;
            throw new IllegalStateException();
        }
    }

    /**
     * 浮点数数组分区迭代器
     * */
    static final class DoubleArraySpliterator implements Spliterator.OfDouble{

        private final double[] array;
        private int index;
        private final int fence;
        private final int characteristics;

        public DoubleArraySpliterator(double[] array,int additionalCharacteristics){
            this(array,0,array.length,additionalCharacteristics);
        }

        public DoubleArraySpliterator(double[] array,int origin,int fence,int additionalCharacteristics){
            this.array = array;
            this.index = origin;
            this.fence = fence;
            this.characteristics = additionalCharacteristics | Spliterator.SIZED | Spliterator.SUBSIZED;
        }

        @Override
        public boolean tryAdvance(DoubleConsumer action) {
            Objects.requireNonNull(action);
            if(index >= 0 && index < fence){
                action.accept(array[index++]);
                return true;
            }
            return false;
        }

        @Override
        public OfDouble trySplit() {
            int lo = index,mid = (fence + lo) >>> 1;
            return lo >= mid ? null : new DoubleArraySpliterator(array,lo,index = mid,characteristics);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void forEachRemaining(DoubleConsumer action){
            Objects.requireNonNull(action);
            double[] a;int i;int hi;
            if((a = array).length >= (hi = fence)
                    && (i = index) >= 0 && i < (index = hi)){
                do{action.accept(a[i]);}while(++i < hi);
            }
        }

        @Override
        public long estimateSize() {
            return (long) (fence - index);
        }

        @Override
        public int characteristics() {
            return characteristics;
        }

        public Comparator<? super Double> getComparator(){
            if(hasCharacteristics(Spliterator.SORTED))
                return null;
            throw new IllegalStateException();
        }
    }

    /**
     * 一个用于分割集合的分区迭代器
     * */
    static final class IteratorSpliterator<T> implements Spliterator<T>{
        static final int BATCH_UNIT = 1 << 10;
        static final int MAX_BATCH = 1 << 25;
        private Collection<? extends T> collection;
        private Iterator<? extends T> it;
        private final int characteristics;
        private long est;
        private int batch;

        public IteratorSpliterator(Collection<? extends T> collection,int characteristics){
            this.collection = collection;
            this.it = null;
            this.characteristics = (characteristics & Spliterator.CONCURRENT) == 0
                    ? characteristics | Spliterator.SIZED | Spliterator.SUBSIZED
                    : characteristics;
        }

        public IteratorSpliterator(Iterator<? extends T> iterator,long size,int characteristics){
            this.collection = null;
            this.it = iterator;
            this.est = size;
            this.characteristics = (characteristics & Spliterator.CONCURRENT) == 0
                    ? characteristics | Spliterator.SIZED | Spliterator.SUBSIZED
                    : characteristics;
        }

        public IteratorSpliterator(Iterator<? extends T> iterator,int characteristics){
            this.collection = null;
            this.it = iterator;
            this.est = Long.MAX_VALUE;
            this.characteristics = characteristics & ~(Spliterator.SIZED | Spliterator.SUBSIZED);
        }

        @Override
        public boolean tryAdvance(Consumer<? super T> action) {
            Objects.requireNonNull(action);
            if(it == null){
                it = collection.iterator();
                est = (long)collection.size();
            }
            if(it.hasNext()){
                action.accept((T)it.next());
                return true;
            }
            return false;
        }

        @Override
        public Spliterator<T> trySplit() {
            Iterator<? extends T> i;
            long s;
            if((i = it) == null){
                i = it = collection.iterator();
                s = est = (long) collection.size();
            }
            else
                s = est;
            if(s > 1 && i.hasNext()){
                int n = batch + BATCH_UNIT;
                if(n > s)
                    n = (int) s;
                if(n > MAX_BATCH)
                    n = MAX_BATCH;
                Object[] a = new Object[n];
                int j = 0;
                do{a[j] = i.next();}while(++j < n && i.hasNext());
                batch = j;
                if(est != Long.MAX_VALUE)
                    est -= j;
                return new ArraySpliterator<>(a,0,j,characteristics);
            }
            return null;
        }

        @Override
        public void forEachRemaining(Consumer<? super T> action){
            Objects.requireNonNull(action);
            Iterator<? extends T> i;
            if ((i = it) == null) {
                i = it = collection.iterator();
                est = (long)collection.size();
            }
            i.forEachRemaining((java.util.function.Consumer<? super T>)action::accept);
        }

        @Override
        public long estimateSize() {
            if(it == null) {
                it = collection.iterator();
                return (long) collection.size();
            }
            return est;
        }

        @Override
        public int characteristics() {
            return characteristics;
        }

        @Override
        public Comparator<? super T> getComparator(){
            if(hasCharacteristics(Spliterator.SORTED))
                return null;
            throw new IllegalStateException();
        }
    }

    /**
     * 一个用于分割整数类型集合的分区迭代器
     * */
    static final class IntIteratorSpliterator implements Spliterator.OfInt{
        static final int BATCH_UNIT = IteratorSpliterator.BATCH_UNIT;
        static final int MAX_BATCH = IteratorSpliterator.MAX_BATCH;
        private PrimitiveIterator.OfInt it;
        private final int characteristics;
        private long est;
        private int batch;

        public IntIteratorSpliterator(PrimitiveIterator.OfInt iterator,long size,int characteristics){
            this.it = iterator;
            this.est = size;
            this.characteristics = (characteristics & Spliterator.CONCURRENT) == 0
                    ? characteristics | Spliterator.SIZED | Spliterator.SUBSIZED
                    : characteristics;
        }

        public IntIteratorSpliterator(PrimitiveIterator.OfInt iterator,int characteristics){
            this.it = iterator;
            this.est = Long.MAX_VALUE;
            this.characteristics = characteristics & ~(Spliterator.SIZED | Spliterator.SUBSIZED);
        }

        @Override
        public boolean tryAdvance(IntConsumer action) {
            Objects.requireNonNull(action);
            if(it.hasNext()){
                action.accept(it.next());
                return true;
            }
            return false;
        }

        @Override
        public Spliterator.OfInt trySplit() {
            PrimitiveIterator.OfInt i = it;
            long s = est;
            if(s > 1 && i.hasNext()){
                int n = batch + BATCH_UNIT;
                if(n > s)
                    n = (int) s;
                if(n > MAX_BATCH)
                    n = MAX_BATCH;
                int[] a = new int[n];
                int j = 0;
                do{a[j] = i.next();}while(++j < n && i.hasNext());
                batch = j;
                if(est != Long.MAX_VALUE)
                    est -= j;
                return new IntArraySpliterator(a,0,j,characteristics);
            }
            return null;
        }

        @Override
        public void forEachRemaining(IntConsumer action){
            Objects.requireNonNull(action);
            it.forEachRemaining(action);
        }

        @Override
        public long estimateSize() {
            return est;
        }

        @Override
        public int characteristics() {
            return characteristics;
        }

        @Override
        public Comparator<? super Integer> getComparator(){
            if(hasCharacteristics(Spliterator.SORTED))
                return null;
            throw new IllegalStateException();
        }
    }

    /**
     * 一个用于分割长整数类型集合的分区迭代器
     * */
    static final class LongIteratorSpliterator implements Spliterator.OfLong{
        static final int BATCH_UNIT = IteratorSpliterator.BATCH_UNIT;
        static final int MAX_BATCH = IteratorSpliterator.MAX_BATCH;
        private PrimitiveIterator.OfLong it;
        private final int characteristics;
        private long est;
        private int batch;

        public LongIteratorSpliterator(PrimitiveIterator.OfLong iterator,long size,int characteristics){
            this.it = iterator;
            this.est = size;
            this.characteristics = (characteristics & Spliterator.CONCURRENT) == 0
                    ? characteristics | Spliterator.SIZED | Spliterator.SUBSIZED
                    : characteristics;
        }

        public LongIteratorSpliterator(PrimitiveIterator.OfLong iterator,int characteristics){
            this.it = iterator;
            this.est = Long.MAX_VALUE;
            this.characteristics = characteristics & ~(Spliterator.SIZED | Spliterator.SUBSIZED);
        }

        @Override
        public boolean tryAdvance(LongConsumer action) {
            Objects.requireNonNull(action);
            if(it.hasNext()){
                action.accept(it.next());
                return true;
            }
            return false;
        }

        @Override
        public Spliterator.OfLong trySplit() {
            PrimitiveIterator.OfLong i = it;
            long s = est;
            if(s > 1 && i.hasNext()){
                int n = batch + BATCH_UNIT;
                if(n > s)
                    n = (int) s;
                if(n > MAX_BATCH)
                    n = MAX_BATCH;
                long[] a = new long[n];
                int j = 0;
                do{a[j] = i.next();}while(++j < n && i.hasNext());
                batch = j;
                if(est != Long.MAX_VALUE)
                    est -= j;
                return new LongArraySpliterator(a,0,j,characteristics);
            }
            return null;
        }

        @Override
        public void forEachRemaining(LongConsumer action){
            Objects.requireNonNull(action);
            it.forEachRemaining(action);
        }

        @Override
        public long estimateSize() {
            return est;
        }

        @Override
        public int characteristics() {
            return characteristics;
        }

        @Override
        public Comparator<? super Long> getComparator(){
            if(hasCharacteristics(Spliterator.SORTED))
                return null;
            throw new IllegalStateException();
        }
    }

    /**
     * 一个用于分割浮点数类型集合的分区迭代器
     * */
    static final class DoubleIteratorSpliterator implements Spliterator.OfDouble{
        static final int BATCH_UNIT = IteratorSpliterator.BATCH_UNIT;
        static final int MAX_BATCH = IteratorSpliterator.MAX_BATCH;
        private PrimitiveIterator.OfDouble it;
        private final int characteristics;
        private long est;
        private int batch;

        public DoubleIteratorSpliterator(PrimitiveIterator.OfDouble iterator,long size,int characteristics){
            this.it = iterator;
            this.est = size;
            this.characteristics = (characteristics & Spliterator.CONCURRENT) == 0
                    ? characteristics | Spliterator.SIZED | Spliterator.SUBSIZED
                    : characteristics;
        }

        public DoubleIteratorSpliterator(PrimitiveIterator.OfDouble iterator,int characteristics){
            this.it = iterator;
            this.est = Long.MAX_VALUE;
            this.characteristics = characteristics & ~(Spliterator.SIZED | Spliterator.SUBSIZED);
        }

        @Override
        public boolean tryAdvance(DoubleConsumer action) {
            Objects.requireNonNull(action);
            if(it.hasNext()){
                action.accept(it.next());
                return true;
            }
            return false;
        }

        @Override
        public Spliterator.OfDouble trySplit() {
            PrimitiveIterator.OfDouble i = it;
            long s = est;
            if(s > 1 && i.hasNext()){
                int n = batch + BATCH_UNIT;
                if(n > s)
                    n = (int) s;
                if(n > MAX_BATCH)
                    n = MAX_BATCH;
                double[] a = new double[n];
                int j = 0;
                do{a[j] = i.next();}while(++j < n && i.hasNext());
                batch = j;
                if(est != Long.MAX_VALUE)
                    est -= j;
                return new DoubleArraySpliterator(a,0,j,characteristics);
            }
            return null;
        }

        @Override
        public void forEachRemaining(DoubleConsumer action){
            Objects.requireNonNull(action);
            it.forEachRemaining(action);
        }

        @Override
        public long estimateSize() {
            return est;
        }

        @Override
        public int characteristics() {
            return characteristics;
        }

        @Override
        public Comparator<? super Double> getComparator(){
            if(hasCharacteristics(Spliterator.SORTED))
                return null;
            throw new IllegalStateException();
        }
    }

    /**
     * Spliterator<T>的抽象类，抽象类没有定义源的类型
     * */
    public static abstract class AbstractSpliterator<T> implements Spliterator<T>{
        static final int BATCH_UNIT = 1 << 10; //每次拆分分区数量的增量
        static final int MAX_BATCH = 1 << 25; //最大的批量数组的大小
        private final int characteristics; //迭代器特征
        private long est; //迭代器估计大小
        private int batch; //记录上一次分区迭代器的大小

        protected AbstractSpliterator(long est,int additionalCharacteristics){
            this.est = est;
            this.characteristics = ((additionalCharacteristics & Spliterator.SIZED) != 0)
                    ? additionalCharacteristics | Spliterator.SUBSIZED
                    : additionalCharacteristics;
        }

        /**
         * 记录被消费元素的Consumer类
         * */
        static final class HoldingConsumer<T> implements Consumer<T>{
            Object value;

            @Override
            public void accept(T t) {
                this.value = value;
            }
        }

        /**
         * 每次返回一个以BATCH_UNIT为增量的Spliterator
         *
         * 举例：
         * 比如一个大小为 1024 * 4 = 4096 的 Spliterator
         * 第一次分离得到一个大小为 1024 的 Spliterator
         * 第二此分离得到一个大小为 1024 + BATCH_UNIT 的 Spliterator
         * 第三次分离由于只剩余1024个元素所以得到一个大小为 1024 的 Spliterator
         * */
        @Override
        public Spliterator<T> trySplit(){
            HoldingConsumer<T> holder = new HoldingConsumer<>();
            long s = est;
            //如果当前迭代器的大小大于1，并且当前分区迭代器的tryAdvance没有指向最后一个元素
            if(s > 1 & tryAdvance(holder)){
                int n = batch + BATCH_UNIT; //当前迭代器在源中的位置加上每次分区的增量BATCH_UNIT
                //如果n大于源中的可被拆分的最大数量，那么n=MAX_BATCH
                if(n > MAX_BATCH)
                    n = MAX_BATCH;
                //如果n大于源大小，n=s
                if(n > s)
                    n = (int) s;
                //创建一个数组用来存储即将从本分区迭代器中继续分离出去的元素
                Object[] a = new Object[n];
                //记录数组大小
                int j = 0;
                //调用tryAdvance读取当前迭代器中的元素，其中消费者HoldingConsumer会记录每个被消费的元素，并把元素值赋值到数组当中
                do{a[j] = holder.value;}while(++j < n && tryAdvance(holder));
                batch = j; //把bacth更新为当前的分区迭代器的大小
                //更新当前迭代器的大小
                if(est != Long.MAX_VALUE)
                    est -= j;
                return new ArraySpliterator<>(a,0,j,characteristics());
            }
            return null;
        }

        @Override
        public long estimateSize(){return est;}

        @Override
        public int characteristics(){return characteristics;}
    }

    /**
     * Spliterator.OfInt的抽象类，抽象类没有定义源的类型
     * */
    public static abstract class AbstractIntSpliterator implements Spliterator.OfInt{
        static final int MAX_BATCH = AbstractSpliterator.MAX_BATCH;
        static final int BATCH_UNIT = AbstractSpliterator.BATCH_UNIT;
        private final int characteristics;
        private long est;
        private int batch;

        protected AbstractIntSpliterator(long est,int additionalCharacteristics){
            this.est = est;
            this.characteristics = ((additionalCharacteristics & Spliterator.SIZED) != 0)
                    ? additionalCharacteristics | Spliterator.SUBSIZED
                    : additionalCharacteristics;
        }

        static final class HoldingIntConsumer implements IntConsumer{
            int value;

            @Override
            public void accept(int value) {
                this.value = value;
            }
        }

        @Override
        public Spliterator.OfInt trySplit(){
            HoldingIntConsumer holder = new HoldingIntConsumer();
            long s = est;
            if(s > 1 & tryAdvance(holder)){
                int n = batch + BATCH_UNIT;
                if(n > MAX_BATCH)
                    n = MAX_BATCH;
                if(n > s)
                    n = (int) s;
                int[] a = new int[n];
                int j = 0;
                do{a[j] = holder.value;}while(++j < n && tryAdvance(holder));
                batch = j;
                if(est != Long.MAX_VALUE)
                    est -= j;
                return new IntArraySpliterator(a,0,j,characteristics());
            }
            return null;
        }

        @Override
        public long estimateSize(){return est;}

        @Override
        public int characteristics(){return characteristics;}
    }

    /**
     * Spliterator.OfLong的抽象类，抽象类没有定义源的类型
     * */
    public static abstract class AbstractLongSpliterator implements Spliterator.OfLong{
        static final int MAX_BATCH = AbstractSpliterator.MAX_BATCH;
        static final int BATCH_UNIT = AbstractSpliterator.BATCH_UNIT;
        private final int characteristics;
        private long est;
        private int batch;

        protected AbstractLongSpliterator(long est,int additionalCharacteristics){
            this.est = est;
            this.characteristics = ((additionalCharacteristics & Spliterator.SIZED) != 0)
                    ? additionalCharacteristics | Spliterator.SUBSIZED
                    : additionalCharacteristics;
        }

        static final class HoldingLongConsumer implements LongConsumer{
            long value;

            @Override
            public void accept(long value) {
                this.value = value;
            }
        }

        @Override
        public Spliterator.OfLong trySplit(){
            HoldingLongConsumer holder = new HoldingLongConsumer();
            long s = est;
            if(s > 1 & tryAdvance(holder)){
                int n = batch + BATCH_UNIT;
                if(n > MAX_BATCH)
                    n = MAX_BATCH;
                if(n > s)
                    n = (int) s;
                long[] a = new long[n];
                int j = 0;
                do{a[j] = holder.value;}while(++j < n && tryAdvance(holder));
                batch = j;
                if(est != Long.MAX_VALUE)
                    est -= j;
                return new LongArraySpliterator(a,0,j,characteristics());
            }
            return null;
        }

        @Override
        public long estimateSize(){return est;}

        @Override
        public int characteristics(){return characteristics;}
    }

    /**
     * Spliterator.OfDouble的抽象类，抽象类没有定义源的类型
     * */
    public static abstract class AbstractDoubleSpliterator implements Spliterator.OfDouble{
        static final int MAX_BATCH = AbstractSpliterator.MAX_BATCH;
        static final int BATCH_UNIT = AbstractSpliterator.BATCH_UNIT;
        private final int characteristics;
        private long est;
        private int batch;

        protected AbstractDoubleSpliterator(long est,int additionalCharacteristics){
            this.est = est;
            this.characteristics = ((additionalCharacteristics & Spliterator.SIZED) != 0)
                    ? additionalCharacteristics | Spliterator.SUBSIZED
                    : additionalCharacteristics;
        }

        static final class HoldingDoubleConsumer implements DoubleConsumer{
            double value;

            @Override
            public void accept(double value) {
                this.value = value;
            }
        }

        @Override
        public Spliterator.OfDouble trySplit(){
            HoldingDoubleConsumer holder = new HoldingDoubleConsumer();
            long s = est;
            if(s > 1 & tryAdvance(holder)){
                int n = batch + BATCH_UNIT;
                if(n > MAX_BATCH)
                    n = MAX_BATCH;
                if(n > s)
                    n = (int) s;
                double[] a = new double[n];
                int j = 0;
                do{a[j] = holder.value;}while(++j < n && tryAdvance(holder));
                batch = j;
                if(est != Long.MAX_VALUE)
                    est -= j;
                return new DoubleArraySpliterator(a,0,j,characteristics());
            }
            return null;
        }

        @Override
        public long estimateSize(){return est;}

        @Override
        public int characteristics(){return characteristics;}
    }
}