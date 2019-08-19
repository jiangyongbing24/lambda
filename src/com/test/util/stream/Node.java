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
    Spliterator<T> getSpliterator();

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

    /** 根据from和to截取节点中的数据组成一个新的节点 */
    default Node<T> truncate(long from,long to,IntFunction<T[]> generator){
        //如果from为0并且to等于Node的大小，直接返回当前节点
        if(from == 0 && to == count())
            return this;
        Spliterator<T> spliterator = getSpliterator();//创建节点的分区迭代器
        long size = to - from;//得到需要截取的长度
        //根据size和generator生成一个具有Sink和Node两个特性的Builder，并且根据size的大小具有不同的存储策略
        Node.Builder<T> nodeBuilder = Nodes.builder(size,generator);
        nodeBuilder.begin(size);//开始当前Node节点中的数据放入nodeBuilder当中
        for(int i=0;i<from && spliterator.tryAdvance(e -> {});i++){}//让spliterator迭代到节点中的from的位置
        for(int i=0;(i<size) && spliterator.tryAdvance(nodeBuilder);i++){}//从节点from开始执行nodeBuilder.accept(e)
        nodeBuilder.end();//结束接受元素
        return nodeBuilder.build();
    }

    /**
     * 把node节点中的数据转换为数组类型
     * */
    T[] asArray(IntFunction<T[]> generator);

    /**
     * array从偏移量offset开始复制节点中的数据到数组array当中
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

    /** 一个表示使用Node作为数据类型的Sink */
    interface Builder<T> extends Sink<T>{
        Node<T> build();

        /** 只接受Integer的Builder */
        interface OfInt extends Node.Builder<Integer>,Sink.OfInt{
            @Override
            Node.OfInt build();
        }

        /** 只接受Long的Builder */
        interface OfLong extends Node.Builder<Long>,Sink.OfLong{
            @Override
            Node.OfLong build();
        }

        /** 只接受Double的Builder */
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
        /** 返回一个分区迭代器 */
        @Override
        T_SPLITR getSpliterator();

        /** 循环 */
        @SuppressWarnings("overloads")
        void forEach(T_CONS action);

        /** 获取子节点 */
        @Override
        default T_NODE getChild(int i){throw new IndexOutOfBoundsException();}

        /** 根据from和to截取节点中的数据组成一个新的节点 */
        T_NODE truncate(long from, long to, IntFunction<T[]> generator);

        /** 把node节点中的数据转换为数组类型 */
        @Override
        default T[] asArray(IntFunction<T[]> generator){
            Objects.requireNonNull(generator);
            if(Tripwire.ENABLED)
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

    /**
     * 一个表示元素都是Integer的Node节点
     * */
    interface OfInt extends OfPrimitive<Integer, IntConsumer,int[],Spliterator.OfInt, OfInt>{
        @Override
        default void forEach(Consumer<? super Integer> action){
            Objects.requireNonNull(action);
            if(action instanceof IntConsumer)
                forEach((IntConsumer) action);
            else{
                if(Tripwire.ENABLED)
                    Tripwire.trip(getClass(),"{0} calling Node.OfInt.forEachRemaining(Consumer)");
                getSpliterator().forEachRemaining(action);
            }
        }

        @Override
        default void copyInto(Integer[] boxed,int offset){
            if (Tripwire.ENABLED)
                Tripwire.trip(getClass(), "{0} calling Node.OfInt.copyInto(Integer[], int)");
            int[] array = asPrimitiveArray();
            for(int i=0;i<array.length;i++){
                boxed[i+offset] = array[i];
            }
        }

        @Override
        default Node.OfInt truncate(long from, long to, IntFunction<Integer[]> generator){
            //如果from为0并且to等于Node的大小，直接返回当前节点
            if(from == 0 && to == count())
                return this;
            Spliterator.OfInt spliterator = getSpliterator();//创建节点的分区迭代器
            long size = to - from;//得到需要截取的长度
            //根据size和generator生成一个具有Sink和Node两个特性的Builder，并且根据size的大小具有不同的存储策略
            Node.Builder.OfInt nodeBuilder = Nodes.intBuilder(size);
            nodeBuilder.begin(size);//开始当前Node节点中的数据放入nodeBuilder当中
            for(int i=0;i<from && spliterator.tryAdvance((IntConsumer) e -> {});i++){}//让spliterator迭代到节点中的from的位置
            for(int i=0;(i<size) && spliterator.tryAdvance((IntConsumer)nodeBuilder);i++){}//从节点from开始执行nodeBuilder.accept(e)
            nodeBuilder.end();//结束接受元素
            return nodeBuilder.build();
        }

        @Override
        default int[] newArray(int count) {
            return new int[count];
        }

        default StreamShape getShape(){return StreamShape.INT_VALUE;}
    }

    /**
     * 一个表示元素都是Long的Node节点
     * */
    interface OfLong extends OfPrimitive<Long, LongConsumer,long[],Spliterator.OfLong, OfLong>{
        @Override
        default void forEach(Consumer<? super Long> action){
            Objects.requireNonNull(action);
            if(action instanceof LongConsumer)
                forEach((LongConsumer) action);
            else{
                if(Tripwire.ENABLED)
                    Tripwire.trip(getClass(),"{0} calling Node.OfInt.forEachRemaining(Consumer)");
                getSpliterator().forEachRemaining(action);
            }
        }

        @Override
        default void copyInto(Long[] boxed,int offset){
            if (Tripwire.ENABLED)
                Tripwire.trip(getClass(), "{0} calling Node.OfInt.copyInto(Integer[], int)");
            long[] array = asPrimitiveArray();
            for(int i=0;i<array.length;i++){
                boxed[i+offset] = array[i];
            }
        }

        @Override
        default Node.OfLong truncate(long from, long to, IntFunction<Long[]> generator){
            //如果from为0并且to等于Node的大小，直接返回当前节点
            if(from == 0 && to == count())
                return this;
            Spliterator.OfLong spliterator = getSpliterator();//创建节点的分区迭代器
            long size = to - from;//得到需要截取的长度
            //根据size和generator生成一个具有Sink和Node两个特性的Builder，并且根据size的大小具有不同的存储策略
            Node.Builder.OfLong nodeBuilder = Nodes.longBuilder(size);
            nodeBuilder.begin(size);//开始当前Node节点中的数据放入nodeBuilder当中
            for(int i=0;i<from && spliterator.tryAdvance((LongConsumer) e -> {});i++){}//让spliterator迭代到节点中的from的位置
            for(int i=0;(i<size) && spliterator.tryAdvance((LongConsumer)nodeBuilder);i++){}//从节点from开始执行nodeBuilder.accept(e)
            nodeBuilder.end();//结束接受元素
            return nodeBuilder.build();
        }

        @Override
        default long[] newArray(int count) {
            return new long[count];
        }

        default StreamShape getShape(){return StreamShape.LONG_VALUE;}
    }

    /**
     * 一个表示元素都是Double的Node节点
     * */
    interface OfDouble extends OfPrimitive<Double, DoubleConsumer,double[],Spliterator.OfDouble, OfDouble>{
        @Override
        default void forEach(Consumer<? super Double> action){
            Objects.requireNonNull(action);
            if(action instanceof DoubleConsumer)
                forEach((DoubleConsumer) action);
            else{
                if(Tripwire.ENABLED)
                    Tripwire.trip(getClass(),"{0} calling Node.OfInt.forEachRemaining(Consumer)");
                getSpliterator().forEachRemaining(action);
            }
        }

        @Override
        default void copyInto(Double[] boxed,int offset){
            if (Tripwire.ENABLED)
                Tripwire.trip(getClass(), "{0} calling Node.OfInt.copyInto(Integer[], int)");
            double[] array = asPrimitiveArray();
            for(int i=0;i<array.length;i++){
                boxed[i+offset] = array[i];
            }
        }

        @Override
        default Node.OfDouble truncate(long from, long to, IntFunction<Double[]> generator){
            //如果from为0并且to等于Node的大小，直接返回当前节点
            if(from == 0 && to == count())
                return this;
            Spliterator.OfDouble spliterator = getSpliterator();//创建节点的分区迭代器
            long size = to - from;//得到需要截取的长度
            //根据size和generator生成一个具有Sink和Node两个特性的Builder，并且根据size的大小具有不同的存储策略
            Node.Builder.OfDouble nodeBuilder = Nodes.doubleBuilder(size);
            nodeBuilder.begin(size);//开始当前Node节点中的数据放入nodeBuilder当中
            for(int i=0;i<from && spliterator.tryAdvance((DoubleConsumer) e -> {});i++){}//让spliterator迭代到节点中的from的位置
            for(int i=0;(i<size) && spliterator.tryAdvance((DoubleConsumer)nodeBuilder);i++){}//从节点from开始执行nodeBuilder.accept(e)
            nodeBuilder.end();//结束接受元素
            return nodeBuilder.build();
        }

        @Override
        default double[] newArray(int count) {
            return new double[count];
        }

        default StreamShape getShape(){return StreamShape.DOUBLE_VALUE;}
    }
}