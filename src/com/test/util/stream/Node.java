package com.test.util.stream;

import com.test.util.Spliterator;
import com.test.util.function.*;

import java.util.Objects;

/**
 * 一个不可变的容器，用于描述某些元素的有序序列
 * 一种多叉树结构，元素存储在树的叶子当中，并且一个叶子节点可以存放多个元素
 */
interface Node<T> {
    /**
     * 返回一个分区迭代器描述Node节点中的元素
     * */
    Spliterator<T> spliterator();

    /**
     * 循环Node中的元素
     * */
    void forEach(Consumer<? super T> action);

    /**
     * 获取子节点数量
     * */
    default int getChildCount(){return 0;}

    /**
     * 根据i获取子节点
     * */
    default Node<T> getChild(int i){throw new IndexOutOfBoundsException();}

    default Node<T> truncate(){
        return null;
    }

    /**
     * 把node节点中的数据转换为数组类型
     * */
    T[] asArray(IntFunction<T[]> generator);

    /**
     * array从偏移量offset开始复制节点中的数据到数组当中
     * */
    void copyInto(T[] array,int offset);

    /**
     * 返回StreamShape
     * */
    default StreamShape getShape(){return StreamShape.REFERENCE;}

    /**
     * 返回此节点中包含的元素的数量
     * */
    long count();

    interface Builder<T> extends Sink<T>{
        Node<T> build();

        interface OfInt extends Node.Builder<Integer>,Sink.OfInt{
            @Override
            Node.OfInt build();
        }

        interface OfLong extends Node.Builder<Long>,Sink.OfLong{
            @Override
            Node.OfLong build();
        }

        interface OfDouble extends Node.Builder<Double>,Sink.OfDouble{
            @Override
            Node.OfDouble build();
        }
    }

    /**
     * 一个表示元素都是包装类的Node节点
     * */
    public interface OfPrimitive<T,T_CONS,T_ARR
            ,T_SPLITR extends Spliterator.OfPrimitive<T,T_CONS,T_SPLITR>
            ,T_NODE extends OfPrimitive<T,T_CONS,T_ARR,T_SPLITR,T_NODE>> extends Node<T>{
        @Override
        T_SPLITR spliterator();

        @SuppressWarnings("overloads")
        void forEach(T_CONS action);

        @Override
        default T_NODE getChild(int i){throw new IndexOutOfBoundsException();}

        T_NODE truncate(long from, long to, IntFunction<T[]> generator);

        @Override
        default T[] asArray(IntFunction<T[]> generator){
            Objects.requireNonNull(generator);
            if(Tripwire.ENABLE)
                Tripwire.trip(getClass(),"{0} calling Node.OfPrimitive.asArray");
            long size = count();
            if(size >= Nodes.MAX_ARRAY_SIZE)
                throw new IllegalStateException(Nodes.BAD_SIZE);
            T[] boxed = generator.apply((int)size);
            copyInto(boxed,0);
            return boxed;
        }

        /**
         * 以数组的形式返回节点数据
         * */
        T_ARR asPrimitiveArray();

        /**
         * 创建一个新的包装类数组
         */
        T_ARR newArray(int count);

        /**
         * array从偏移量offset开始复制节点中的数据到数组当中
         * */
        void copyInto(T_ARR array,int offset);
    }

    interface OfInt extends OfPrimitive<Integer, IntConsumer,int[],Spliterator.OfInt, OfInt>{
        @Override
        default void forEach(Consumer<? super Integer> action){
            Objects.requireNonNull(action);
            if(action instanceof IntConsumer)
                forEach((IntConsumer) action);
            else{
                if(Tripwire.ENABLE)
                    Tripwire.trip(getClass(),"{0} calling Node.OfInt.forEachRemaining(Consumer)");
                forEach((IntConsumer) action::accept);
            }
        }

        @Override
        default void copyInto(Integer[] boxed,int offset){
            if (Tripwire.ENABLE)
                Tripwire.trip(getClass(), "{0} calling Node.OfInt.copyInto(Integer[], int)");
            int[] array = asPrimitiveArray();
            for(int i=0;i<array.length;i++){
                boxed[i+offset] = array[i];
            }
        }
    }

    interface OfLong extends OfPrimitive<Long, LongConsumer,long[],Spliterator.OfLong, OfLong>{}

    interface OfDouble extends OfPrimitive<Double, DoubleConsumer,double[],Spliterator.OfDouble, OfDouble>{}
}