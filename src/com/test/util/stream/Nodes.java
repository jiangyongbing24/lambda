package com.test.util.stream;

import com.test.util.Arrays;
import com.test.util.Spliterator;
import com.test.util.function.Consumer;
import com.test.util.function.IntFunction;

import java.util.Objects;

/**
 * 提供各种Node节点
 * */
final class Nodes {
    static final long MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    static final String BAD_SIZE = "Stream size exceeds max array size";

    /**
     * 生产一个Node.Builder
     * */
    static <T> Node.Builder<T> builder(long exactSizeIfKnown, IntFunction<T[]> generator){
        return (exactSizeIfKnown >= 0 && exactSizeIfKnown < MAX_ARRAY_SIZE)
                ? new FixedNodeBuilder<>(exactSizeIfKnown,generator)
                : null;
    }

    private static final class FixedNodeBuilder<T>
            extends ArrayNode<T>
            implements Node.Builder<T> {
        FixedNodeBuilder(long size, IntFunction<T[]> generator) {
            super(size, generator);
            assert curSize < MAX_ARRAY_SIZE;
        }

        @Override
        public Node<T> build() {
            if(curSize > MAX_ARRAY_SIZE)
                throw new IllegalStateException(String.format("Current size %d is less than fixed size %d",
                        curSize, array.length));
            return this;
        }

        @Override
        public void begin(long size){
            if(size != array.length)
                throw new IllegalStateException(String.format("Begin size %d is not equal to fixed size %d",
                        size, array.length));
            curSize = 0;
        }

        @Override
        public void accept(T t) {
            if(curSize < array.length)
                array[++curSize] = t;
            else
                throw new IllegalStateException(String.format("Accept exceeded fixed size of %d",
                        array.length));
        }

        @Override
        public void end(){
            if(curSize < array.length)
                throw new IllegalStateException(String.format("End size %d is less than fixed size %d",
                        curSize, array.length));
        }

        @Override
        public String toString() {
            return String.format("FixedNodeBuilder[%d][%s]",
                    array.length - curSize, Arrays.toString(array));
        }
    }

    /** 参考数组的节点类 */
    private static class ArrayNode<T> implements Node<T>{
        final T[] array;
        int curSize;

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
        public Spliterator<T> spliterator() {
            return Arrays.spliterator(array,0,curSize);
        }

        @Override
        public void forEach(Consumer<? super T> action) {
            Objects.requireNonNull(action);
            for(int i=0;i<array.length;i++){
                action.accept(array[i]);
            }
        }

        @Override
        public T[] asArray(IntFunction<T[]> generator) {
            Objects.requireNonNull(generator);
            if(array.length == curSize)
                return array;
            else
                throw new IllegalStateException();
        }

        @Override
        public void copyInto(T[] dest, int destOffset) {
            System.arraycopy(array,0,dest,destOffset,curSize);
        }

        @Override
        public long count() {
            return curSize;
        }

        @Override
        public String toString(){
            return String.format("ArrayNode[%d%][%s%]",array.length-curSize,Arrays.toString(array));
        }
    }
}
