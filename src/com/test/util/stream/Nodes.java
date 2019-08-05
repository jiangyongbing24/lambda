package com.test.util.stream;

import com.test.util.Arrays;
import com.test.util.Spliterator;
import com.test.util.function.*;

import java.util.Objects;

/**
 * 提供各种Node节点
 * */
final class Nodes {
    static final long MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    static final String BAD_SIZE = "Stream size exceeds max array size";

    /**
     * 生产一个Node.Builder
     *
     * 如果容量在[0,MAX_ARRAY_SIZE)区间，创建一个 FixedNodeBuilder
     * 否则创建一个容量可扩充的 SpinedNodeBuilder
     * */
    static <T> Node.Builder<T> builder(long exactSizeIfKnown, IntFunction<T[]> generator){
        return (exactSizeIfKnown >= 0 && exactSizeIfKnown < MAX_ARRAY_SIZE)
                ? new FixedNodeBuilder<>(exactSizeIfKnown,generator)
                : builder();
    }

    /**
     * 生产一个Node.Builder.OfInt
     * */
    static <T> Node.Builder.OfInt intBuilder(long exactSizeIfKnown){
        return (exactSizeIfKnown >= 0 && exactSizeIfKnown < MAX_ARRAY_SIZE)
                ? new IntFixedNodeBuilder(exactSizeIfKnown)
                : intBuilder();
    }

    /**
     * 生产一个Node.Builder.OfLong
     * */
    static <T> Node.Builder.OfLong longBuilder(long exactSizeIfKnown){
        return (exactSizeIfKnown >= 0 && exactSizeIfKnown < MAX_ARRAY_SIZE)
                ? new LongFixedNodeBuilder(exactSizeIfKnown)
                : longBuilder();
    }

    /**
     * 生产一个Node.Builder.OfDouble
     * */
    static <T> Node.Builder.OfDouble doubleBuilder(long exactSizeIfKnown){
        return (exactSizeIfKnown >= 0 && exactSizeIfKnown < MAX_ARRAY_SIZE)
                ? new DoubleFixedNodeBuilder(exactSizeIfKnown)
                : doubleBuilder();
    }

    /** 创造一个SpinedNodeBuilder */
    static <T> Node.Builder<T> builder() {
        return new SpinedNodeBuilder<>();
    }

    /** 创造一个IntSpinedNodeBuilder */
    static <T> Node.Builder.OfInt intBuilder() {
        return new IntSpinedNodeBuilder();
    }

    /** 创造一个LongSpinedNodeBuilder */
    static <T> Node.Builder.OfLong longBuilder() {
        return new LongSpinedNodeBuilder();
    }

    /** 创造一个DoubleSpinedNodeBuilder */
    static <T> Node.Builder.OfDouble doubleBuilder() {
        return new DoubleSpinedNodeBuilder();
    }

    /** 一个具有Sink功能的和使用数组存储Node节点 */
    private static final class FixedNodeBuilder<T>
            extends ArrayNode<T>
            implements Node.Builder<T> {
        /** 通过传入数组大小和一个数组生成器初始化FixedNodeBuilder */
        FixedNodeBuilder(long size, IntFunction<T[]> generator) {
            super(size, generator);
            assert curSize < MAX_ARRAY_SIZE;
        }

        /** 返回当前Node节点 */
        @Override
        public Node<T> build() {
            if(curSize > MAX_ARRAY_SIZE)
                throw new IllegalStateException(String.format("Current size %d is less than fixed size %d",
                        curSize, array.length));
            return this;
        }

        /** 开始构建 */
        @Override
        public void begin(long size){
            if(size != array.length)
                throw new IllegalStateException(String.format("Begin size %d is not equal to fixed size %d",
                        size, array.length));
            curSize = 0;
        }

        /** 接受一个元素进入array数组 */
        @Override
        public void accept(T t) {
            if(curSize < array.length)
                array[++curSize] = t;
            else
                throw new IllegalStateException(String.format("Accept exceeded fixed size of %d",
                        array.length));//如果array的容量不够，抛出异常
        }

        /** 结束构建 */
        @Override
        public void end(){
            if(curSize < array.length)
                throw new IllegalStateException(String.format("End size %d is less than fixed size %d",
                        curSize, array.length));//如果没有array没有填满，抛出异常
        }

        @Override
        public String toString() {
            return String.format("FixedNodeBuilder[%d][%s]",
                    array.length - curSize, Arrays.toString(array));
        }
    }

    /** 只接受int元素的FixedNodeBuilder */
    private static final class IntFixedNodeBuilder
            extends IntArrayNode
            implements Node.Builder.OfInt {
        /** 通过传入数组大小和一个数组生成器初始化IntFixedNodeBuilder */
        IntFixedNodeBuilder(long size) {
            super(size);
            assert curSize < MAX_ARRAY_SIZE;
        }

        /** 返回当前Node节点 */
        @Override
        public Node.OfInt build() {
            if(curSize > MAX_ARRAY_SIZE)
                throw new IllegalStateException(String.format("Current size %d is less than fixed size %d",
                        curSize, array.length));
            return this;
        }

        /** 开始构建 */
        @Override
        public void begin(long size){
            if(size != array.length)
                throw new IllegalStateException(String.format("Begin size %d is not equal to fixed size %d",
                        size, array.length));
            curSize = 0;
        }

        /** 接受一个元素进入array数组 */
        @Override
        public void accept(int t) {
            if(curSize < array.length)
                array[++curSize] = t;
            else
                throw new IllegalStateException(String.format("Accept exceeded fixed size of %d",
                        array.length));//如果array的容量不够，抛出异常
        }

        /** 结束构建 */
        @Override
        public void end(){
            if(curSize < array.length)
                throw new IllegalStateException(String.format("End size %d is less than fixed size %d",
                        curSize, array.length));//如果没有array没有填满，抛出异常
        }

        @Override
        public String toString() {
            return String.format("IntFixedNodeBuilder[%d][%s]",
                    array.length - curSize, Arrays.toString(array));
        }
    }

    /** 只接受long元素的FixedNodeBuilder */
    private static final class LongFixedNodeBuilder
            extends LongArrayNode
            implements Node.Builder.OfLong {
        /** 通过传入数组大小和一个数组生成器初始化LongFixedNodeBuilder */
        LongFixedNodeBuilder(long size) {
            super(size);
            assert curSize < MAX_ARRAY_SIZE;
        }

        /** 返回当前Node节点 */
        @Override
        public Node.OfLong build() {
            if(curSize > MAX_ARRAY_SIZE)
                throw new IllegalStateException(String.format("Current size %d is less than fixed size %d",
                        curSize, array.length));
            return this;
        }

        /** 开始构建 */
        @Override
        public void begin(long size){
            if(size != array.length)
                throw new IllegalStateException(String.format("Begin size %d is not equal to fixed size %d",
                        size, array.length));
            curSize = 0;
        }

        /** 接受一个元素进入array数组 */
        @Override
        public void accept(long t) {
            if(curSize < array.length)
                array[++curSize] = t;
            else
                throw new IllegalStateException(String.format("Accept exceeded fixed size of %d",
                        array.length));//如果array的容量不够，抛出异常
        }

        /** 结束构建 */
        @Override
        public void end(){
            if(curSize < array.length)
                throw new IllegalStateException(String.format("End size %d is less than fixed size %d",
                        curSize, array.length));//如果没有array没有填满，抛出异常
        }

        @Override
        public String toString() {
            return String.format("LongFixedNodeBuilder[%d][%s]",
                    array.length - curSize, Arrays.toString(array));
        }
    }

    /** 只接受double元素的FixedNodeBuilder */
    private static final class DoubleFixedNodeBuilder
            extends DoubleArrayNode
            implements Node.Builder.OfDouble {
        /** 通过传入数组大小和一个数组生成器初始化DoubleFixedNodeBuilder */
        DoubleFixedNodeBuilder(long size) {
            super(size);
            assert curSize < MAX_ARRAY_SIZE;
        }

        /** 返回当前Node节点 */
        @Override
        public Node.OfDouble build() {
            if(curSize > MAX_ARRAY_SIZE)
                throw new IllegalStateException(String.format("Current size %d is less than fixed size %d",
                        curSize, array.length));
            return this;
        }

        /** 开始构建 */
        @Override
        public void begin(long size){
            if(size != array.length)
                throw new IllegalStateException(String.format("Begin size %d is not equal to fixed size %d",
                        size, array.length));
            curSize = 0;
        }

        /** 接受一个元素进入array数组 */
        @Override
        public void accept(double t) {
            if(curSize < array.length)
                array[++curSize] = t;
            else
                throw new IllegalStateException(String.format("Accept exceeded fixed size of %d",
                        array.length));//如果array的容量不够，抛出异常
        }

        /** 结束构建 */
        @Override
        public void end(){
            if(curSize < array.length)
                throw new IllegalStateException(String.format("End size %d is less than fixed size %d",
                        curSize, array.length));//如果没有array没有填满，抛出异常
        }

        @Override
        public String toString() {
            return String.format("DoubleFixedNodeBuilder[%d][%s]",
                    array.length - curSize, Arrays.toString(array));
        }
    }

    /** 参考数组的节点类 */
    private static class ArrayNode<T> implements Node<T>{
        final T[] array;//用力存储节点中的数据
        int curSize;//下一个写入的索引，也就是说实际含有数据的有效索引位置区间是[0,curSize)

        /**
         * 通过传入数组大小和一个数组生成器初始化ArrayNode
         * */
        @SuppressWarnings("unchecked")
        ArrayNode(long size,IntFunction<T[]> generator){
            if(size > MAX_ARRAY_SIZE)
                throw new IllegalStateException(BAD_SIZE);
            this.array = generator.apply((int)size);
            this.curSize = 0;
        }

        /**
         * 通过传入数组初始化ArrayNode
         * */
        ArrayNode(T[] array){
            this.array = array;
            this.curSize = array.length;
        }

        /** 返回一个数组分区迭代器 */
        @Override
        public Spliterator<T> getSpliterator() {
            return Arrays.spliterator(array,0,curSize);
        }

        @Override
        public void forEach(Consumer<? super T> action) {
            Objects.requireNonNull(action);
            for(int i=0;i<array.length;i++){
                action.accept(array[i]);
            }
        }

        /** 根据数组生成器generator生成一个数组，然后把当前Node节点中的数据复制进入这个数组，最后返回数组 */
        @Override
        public T[] asArray(IntFunction<T[]> generator) {
            Objects.requireNonNull(generator);
            if(array.length == curSize)
                return array;
            else
                throw new IllegalStateException();
        }

        /** 把当前的节点中的数据从索引destOffset开始写入dest */
        @Override
        public void copyInto(T[] dest, int destOffset) {
            System.arraycopy(array,0,dest,destOffset,curSize);
        }

        /** 返回节点中元素数量 */
        @Override
        public long count() {
            return curSize;
        }

        @Override
        public String toString(){
            return String.format("ArrayNode[%d%][%s%]",array.length-curSize,Arrays.toString(array));
        }
    }

    /** 参考int数组的节点类 */
    private static class IntArrayNode implements Node.OfInt{
        final int[] array;//用力存储节点中的数据
        int curSize;//下一个写入的索引，也就是说实际含有数据的有效索引位置区间是[0,curSize)

        /**
         * 通过传入数组大小和一个数组生成器初始化ArrayNode
         * */
        IntArrayNode(long size){
            if(size > MAX_ARRAY_SIZE)
                throw new IllegalStateException(BAD_SIZE);
            this.array = new int[(int)size];
            this.curSize = 0;
        }

        /**
         * 通过传入数组初始化ArrayNode
         * */
        IntArrayNode(int[] array){
            this.array = array;
            this.curSize = array.length;
        }

        /** 返回一个数组分区迭代器 */
        @Override
        public Spliterator.OfInt getSpliterator() {
            return Arrays.spliterator(array,0,curSize);
        }

        @Override
        public void forEach(IntConsumer action) {
            Objects.requireNonNull(action);
            for(int i=0;i<array.length;i++){
                action.accept(array[i]);
            }
        }

        /** 根据数组生成器generator生成一个数组，然后把当前Node节点中的数据复制进入这个数组，最后返回数组 */
        @Override
        public int[] asPrimitiveArray() {
            if(array.length == curSize)
                return array;
            else
                return Arrays.copyOf(array, curSize);
        }

        /** 把当前的节点中的数据从索引destOffset开始写入dest */
        @Override
        public void copyInto(int[] dest, int destOffset) {
            System.arraycopy(array,0,dest,destOffset,curSize);
        }

        /** 返回节点中元素数量 */
        @Override
        public long count() {
            return curSize;
        }

        @Override
        public String toString(){
            return String.format("IntArrayNode[%d%][%s%]",array.length-curSize,Arrays.toString(array));
        }
    }

    /** 参考long数组的节点类 */
    private static class LongArrayNode implements Node.OfLong{
        final long[] array;//用力存储节点中的数据
        int curSize;//下一个写入的索引，也就是说实际含有数据的有效索引位置区间是[0,curSize)

        /**
         * 通过传入数组大小和一个数组生成器初始化ArrayNode
         * */
        LongArrayNode(long size){
            if(size > MAX_ARRAY_SIZE)
                throw new IllegalStateException(BAD_SIZE);
            this.array = new long[(int)size];
            this.curSize = 0;
        }

        /**
         * 通过传入数组初始化ArrayNode
         * */
        LongArrayNode(long[] array){
            this.array = array;
            this.curSize = array.length;
        }

        /** 返回一个数组分区迭代器 */
        @Override
        public Spliterator.OfLong getSpliterator() {
            return Arrays.spliterator(array,0,curSize);
        }

        @Override
        public void forEach(LongConsumer action) {
            Objects.requireNonNull(action);
            for(int i=0;i<array.length;i++){
                action.accept(array[i]);
            }
        }

        /** 根据数组生成器generator生成一个数组，然后把当前Node节点中的数据复制进入这个数组，最后返回数组 */
        @Override
        public long[] asPrimitiveArray() {
            if(array.length == curSize)
                return array;
            else
                return Arrays.copyOf(array, curSize);
        }

        /** 把当前的节点中的数据从索引destOffset开始写入dest */
        @Override
        public void copyInto(long[] dest, int destOffset) {
            System.arraycopy(array,0,dest,destOffset,curSize);
        }

        /** 返回节点中元素数量 */
        @Override
        public long count() {
            return curSize;
        }

        @Override
        public String toString(){
            return String.format("LongArrayNode[%d%][%s%]",array.length-curSize,Arrays.toString(array));
        }
    }

    /** 参考double数组的节点类 */
    private static class DoubleArrayNode implements Node.OfDouble{
        final double[] array;//用力存储节点中的数据
        int curSize;//下一个写入的索引，也就是说实际含有数据的有效索引位置区间是[0,curSize)

        /**
         * 通过传入数组大小和一个数组生成器初始化ArrayNode
         * */
        DoubleArrayNode(long size){
            if(size > MAX_ARRAY_SIZE)
                throw new IllegalStateException(BAD_SIZE);
            this.array = new double[(int)size];
            this.curSize = 0;
        }

        /**
         * 通过传入数组初始化ArrayNode
         * */
        DoubleArrayNode(double[] array){
            this.array = array;
            this.curSize = array.length;
        }

        /** 返回一个数组分区迭代器 */
        @Override
        public Spliterator.OfDouble getSpliterator() {
            return Arrays.spliterator(array,0,curSize);
        }

        @Override
        public void forEach(DoubleConsumer action) {
            Objects.requireNonNull(action);
            for(int i=0;i<array.length;i++){
                action.accept(array[i]);
            }
        }

        /** 根据数组生成器generator生成一个数组，然后把当前Node节点中的数据复制进入这个数组，最后返回数组 */
        @Override
        public double[] asPrimitiveArray() {
            if(array.length == curSize)
                return array;
            else
                return Arrays.copyOf(array, curSize);
        }

        /** 把当前的节点中的数据从索引destOffset开始写入dest */
        @Override
        public void copyInto(double[] dest, int destOffset) {
            System.arraycopy(array,0,dest,destOffset,curSize);
        }

        /** 返回节点中元素数量 */
        @Override
        public long count() {
            return curSize;
        }

        @Override
        public String toString(){
            return String.format("DoubleArrayNode[%d%][%s%]",array.length-curSize,Arrays.toString(array));
        }
    }

    /** 一个具有SpinedBuffer缓冲和Sink功能的Node节点 */
    private static final class SpinedNodeBuilder<T>
            extends SpinedBuffer<T>
            implements Node<T>,Node.Builder<T>{
        //标记SpinedNodeBuilder是否处在构建过程
        private boolean building = false;

        SpinedNodeBuilder(){} // 避免创建特殊的访问者

        /** 返回一个SpinedBuffer的分区迭代器 */
        @Override
        public Spliterator<T> getSpliterator(){
            assert !building : "during building";//如果在构建过程中，抛出异常
            return super.getSpliterator();
        }

        @Override
        public void forEach(Consumer<? super T> consumer) {
            assert !building : "during building";
            super.forEach(consumer);
        }

        /** 开始构建 */
        @Override
        public void begin(long size){
            assert !building : "was already building";//如果在构建过程中，抛出异常
            building = true;//标记节点正在构建
            clear();//清空缓存
            ensureCapacity(size);//确保缓冲区至少具有足够的大小的容量去容纳目标大小
        }

        /** 接受一个元素进入节点 */
        @Override
        public void accept(T t){
            assert building : "not building";//如果不在构建当中，不能接受元素，抛出异常
            super.accept(t);
        }

        @Override
        public void end(){
            assert building : "was not building";//如果不在构建当中，不能结束操作，抛出异常
            building = false;//标记构建结束
        }

        @Override
        public void copyInto(T[] array, int offset){
            assert !building : "during building";//如果在构建当中，不能复制，抛出异常
            super.copyInto(array, offset);
        }

        @Override
        public T[] asArray(IntFunction<T[]> arrayFactory) {
            assert !building : "during building";//如果在构建当中，不能操作数据，抛出异常
            return super.asArray(arrayFactory);
        }

        /** 返回当前节点 */
        @Override
        public Node<T> build(){
            assert !building : "during building";//如果在构建当中，不能返回当前节点，抛出异常
            return this;
        }
    }

    /** 只接受int元素的SpinedNodeBuilder */
    private static final class IntSpinedNodeBuilder
            extends SpinedBuffer.OfInt
            implements Node.OfInt,Node.Builder.OfInt{
        //标记SpinedNodeBuilder是否处在构建过程
        private boolean building = false;

        IntSpinedNodeBuilder(){} // 避免创建特殊的访问者

        /** 返回一个IntSpinedNodeBuilder的分区迭代器 */
        @Override
        public Spliterator.OfInt getSpliterator(){
            assert !building : "during building";//如果在构建过程中，抛出异常
            return super.getSpliterator();
        }

        @Override
        public void forEach(IntConsumer consumer) {
            assert !building : "during building";
            super.forEach(consumer);
        }

        /** 开始构建 */
        @Override
        public void begin(long size){
            assert !building : "was already building";//如果在构建过程中，抛出异常
            building = true;//标记节点正在构建
            clear();//清空缓存
            ensureCapacity(size);//确保缓冲区至少具有足够的大小的容量去容纳目标大小
        }

        /** 接受一个元素进入节点 */
        @Override
        public void accept(int t){
            assert building : "not building";//如果不在构建当中，不能接受元素，抛出异常
            super.accept(t);
        }

        @Override
        public void end(){
            assert building : "was not building";//如果不在构建当中，不能结束操作，抛出异常
            building = false;//标记构建结束
        }

        @Override
        public void copyInto(int[] array, int offset) throws IndexOutOfBoundsException {
            assert !building : "during building";//如果在构建当中，不能复制，抛出异常
            super.copyInto(array, offset);
        }

        @Override
        public int[] asPrimitiveArray() {
            assert !building : "during building";//如果在构建当中，不能操作数据，抛出异常
            return super.asPrimitiveArray();
        }

        /** 返回当前节点 */
        @Override
        public Node.OfInt build(){
            assert !building : "during building";//如果在构建当中，不能返回当前节点，抛出异常
            return this;
        }
    }

    /** 只接受long元素的SpinedNodeBuilder */
    private static final class LongSpinedNodeBuilder
            extends SpinedBuffer.OfLong
            implements Node.OfLong,Node.Builder.OfLong{
        //标记SpinedNodeBuilder是否处在构建过程
        private boolean building = false;

        LongSpinedNodeBuilder(){} // 避免创建特殊的访问者

        /** 返回一个IntSpinedNodeBuilder的分区迭代器 */
        @Override
        public Spliterator.OfLong getSpliterator(){
            assert !building : "during building";//如果在构建过程中，抛出异常
            return super.getSpliterator();
        }

        @Override
        public void forEach(LongConsumer consumer) {
            assert !building : "during building";
            super.forEach(consumer);
        }

        /** 开始构建 */
        @Override
        public void begin(long size){
            assert !building : "was already building";//如果在构建过程中，抛出异常
            building = true;//标记节点正在构建
            clear();//清空缓存
            ensureCapacity(size);//确保缓冲区至少具有足够的大小的容量去容纳目标大小
        }

        /** 接受一个元素进入节点 */
        @Override
        public void accept(long t){
            assert building : "not building";//如果不在构建当中，不能接受元素，抛出异常
            super.accept(t);
        }

        @Override
        public void end(){
            assert building : "was not building";//如果不在构建当中，不能结束操作，抛出异常
            building = false;//标记构建结束
        }

        @Override
        public void copyInto(long[] array, int offset) throws IndexOutOfBoundsException {
            assert !building : "during building";//如果在构建当中，不能复制，抛出异常
            super.copyInto(array, offset);
        }

        @Override
        public long[] asPrimitiveArray() {
            assert !building : "during building";//如果在构建当中，不能操作数据，抛出异常
            return super.asPrimitiveArray();
        }

        /** 返回当前节点 */
        @Override
        public Node.OfLong build(){
            assert !building : "during building";//如果在构建当中，不能返回当前节点，抛出异常
            return this;
        }
    }

    /** 只接受double元素的SpinedNodeBuilder */
    private static final class DoubleSpinedNodeBuilder
            extends SpinedBuffer.OfDouble
            implements Node.OfDouble,Node.Builder.OfDouble{
        //标记SpinedNodeBuilder是否处在构建过程
        private boolean building = false;

        DoubleSpinedNodeBuilder(){} // 避免创建特殊的访问者

        /** 返回一个IntSpinedNodeBuilder的分区迭代器 */
        @Override
        public Spliterator.OfDouble getSpliterator(){
            assert !building : "during building";//如果在构建过程中，抛出异常
            return super.getSpliterator();
        }

        @Override
        public void forEach(DoubleConsumer consumer) {
            assert !building : "during building";
            super.forEach(consumer);
        }

        /** 开始构建 */
        @Override
        public void begin(long size){
            assert !building : "was already building";//如果在构建过程中，抛出异常
            building = true;//标记节点正在构建
            clear();//清空缓存
            ensureCapacity(size);//确保缓冲区至少具有足够的大小的容量去容纳目标大小
        }

        /** 接受一个元素进入节点 */
        @Override
        public void accept(double t){
            assert building : "not building";//如果不在构建当中，不能接受元素，抛出异常
            super.accept(t);
        }

        @Override
        public void end(){
            assert building : "was not building";//如果不在构建当中，不能结束操作，抛出异常
            building = false;//标记构建结束
        }

        @Override
        public void copyInto(double[] array, int offset) throws IndexOutOfBoundsException {
            assert !building : "during building";//如果在构建当中，不能复制，抛出异常
            super.copyInto(array, offset);
        }

        @Override
        public double[] asPrimitiveArray() {
            assert !building : "during building";//如果在构建当中，不能操作数据，抛出异常
            return super.asPrimitiveArray();
        }

        /** 返回当前节点 */
        @Override
        public Node.OfDouble build(){
            assert !building : "during building";//如果在构建当中，不能返回当前节点，抛出异常
            return this;
        }
    }
}
