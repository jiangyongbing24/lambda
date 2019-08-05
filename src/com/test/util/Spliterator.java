package com.test.util;

import com.test.lang.IllegalStateException;
import com.test.util.function.Consumer;
import com.test.util.function.DoubleConsumer;
import com.test.util.function.IntConsumer;
import com.test.util.function.LongConsumer;

/**
 * 分区遍历迭代器
 * */
public interface Spliterator<T>{

    /**
     * 分区中如果存在剩余的元素，执行消费者action，
     * 并且迭代器指向分区内的下一个
     * 如果是最后一个元素，返回false
     * */
    boolean tryAdvance(Consumer<? super T> action);

    /**
     * 对分区内的每个元素执行action
     * 如果分区存在{Spliterator.ORDERED}特征值，那么循环按照{Spliterator.ORDERED}定义的顺序执行
     * */
    default void forEachRemaining(Consumer<? super T> action){
        do{}while(tryAdvance(action));
    }

    /**
     * 对分区再进行一次分区操作，返回一个新的分区迭代器
     * */
    Spliterator<T> trySplit();

    /**
     * 返回元素的估计值
     * */
    long estimateSize();

    /**
     * 如果分区的组合特征值和{Spliterator.SIZED}进行与运算为0，返回-1，否则返回估计大小
     * */
    default long getExactSizeIfKnown(){return ( characteristics() & SIZED ) == 0 ? -1L : estimateSize();}

    /**
     * 返回Spliterator的组合特征值
     *
     * 这个组合特征值是选取
     * {Spliterator.ORDER},{Spliterator.DISTINCT},{Spliterator.SORTED},{Spliterator.SIZED},
     * {Spliterator.NONNULL},{Spliterator.IMMUTABLE},{Spliterator.CONCURRENT},{Spliterator.SUBSIZED}
     * 中的部分特征值进行或运算之后得到的
     * */
    int characteristics();

    /**
     * 如果分区的组合特征值和characteristics进行与运算等于characteristics，返回true，否则返回false
     * */
    default boolean hasCharacteristics(int characteristics){
        return (characteristics() & characteristics) == characteristics;
    }

    /**
     * 如果此Spliterator的源是通过Comparator排序，那么返回这个Comparator比较器
     * 如果此Spliterator的源是通过Comparable排序的，返回null
     * 如果源不能排序，抛出IllegalStateException(非法状态)
     * */
    default Comparator<? super T> getComparator(){throw new IllegalStateException();}

    /**
     * 特征值:表示分区元素是有序的(每一次遍历结果相同)
     * */
    public static final int ORDERED = 0x00000010;

    /**
     * 特征值:表示分区元素是不能重复的
     * */
    public static final int DISTINCT = 0x00000001;

    /**
     * 特征值:表示分区元素按一定规律排列(有指定比较器)
     * */
    public static final int SORTED = 0x00000004;

    /**
     * 特征值:表示分区确定了大小
     * */
    public static final int SIZED = 0x00000040;

    /**
     * 特征值:表示分区中没有NULL元素
     * */
    public static final int NONULL = 0x00000100;

    /**
     * 特征值:表示分区元素不能添加替换或删除
     * */
    public static final int IMMUTABLE = 0x00000400;

    /**
     * 特征值:表示分区元素可以被不同的线程同时访问，它是线程安全的
     * */
    public static final int CONCURRENT = 0x00001000;

    /**
     * 特征值:表示分区的子分区全部具有SIZE特征值
     * */
    public static final int SUBSIZED = 0x00004000;

    /**
     * 继承Spliterator<T>接口
     * 专门用于基本类型的Spliterator，即Spliterator<T>中的T只能为基本类型(int,double...)的包装类(Integer,Double...)
     * */
    public interface OfPrimitive<T,T_CONS,T_SPLITR extends Spliterator.OfPrimitive<T,T_CONS,T_SPLITR>>
            extends Spliterator<T>{
        @Override
        T_SPLITR trySplit();

        /**
         * 这个方法是 boolean tryAdvance(Consumer<? super T> action)的重载
         * */
        @SuppressWarnings("overloads")
        boolean tryAdvance(T_CONS action);

        /**
         * 这个方法是forEachRemaining(Consumer<? super T> action)的重载
         * */
        @SuppressWarnings("overloads")
        default void forEachRemaining(T_CONS action){do{}while(tryAdvance(action));}
    }

    /**
     * 继承OfPrimitive接口，并且接受的包装类是Integer
     * 对应的消费者为IntConsumer
     * */
    public interface OfInt extends OfPrimitive<Integer, IntConsumer,OfInt>{
        @Override
        OfInt trySplit();

        /**
         * 对OfPrimitive中的boolean tryAdvance(T_CONS action)的重写
         * */
        @Override
        boolean tryAdvance(IntConsumer action);

        /**
         * 对OfPrimitive中的default void forEachRemaining(T_CONS action)的重写
         * */
        @Override
        default void forEachRemaining(IntConsumer action){do{}while(tryAdvance(action));}

        /**
         * 这个方法是Spliterator的boolean tryAdvance(Consumer<? super T> action)的重写
         * */
        @Override
        default boolean tryAdvance(Consumer<? super Integer> action){
            if(action instanceof IntConsumer){
                return tryAdvance((IntConsumer) action);
            }
            else{
                if(Tripwire.ENABLE)
                    Tripwire.trip(getClass(),"{0} calling Spliterator.OfInt.tryAdvance((IntConsumer) action::accept)");
                return tryAdvance((IntConsumer)action::accept);
            }
        }

        /**
         * 这个方法是Spliterator的void forEachRemaining(Consumer<? super T> action)的重写
         * */
        @Override
        default void forEachRemaining(Consumer<? super Integer> action){
            if(action instanceof IntConsumer){
                forEachRemaining((IntConsumer) action);
            }
            else{
                if(Tripwire.ENABLE)
                    Tripwire.trip(getClass(),"{0} calling Spliterator.OfInt.forEachRemaining((IntConsumer) action::accept)");
                forEachRemaining((IntConsumer) action::accept);
            }
        }
    }

    /**
     * 继承OfPrimitive接口，并且接受的包装类是Long
     * 对应的消费者为LongConsumer
     * */
    public interface OfLong extends OfPrimitive<Long, LongConsumer, OfLong>{
        @Override
        OfLong trySplit();

        /**
         * 对OfPrimitive中的boolean tryAdvance(T_CONS action)的重写
         * */
        @Override
        boolean tryAdvance(LongConsumer action);

        /**
         * 对OfPrimitive中的default void forEachRemaining(T_CONS action)的重写
         * */
        @Override
        default void forEachRemaining(LongConsumer action){do{}while(tryAdvance(action));}

        /**
         * 这个方法是Spliterator的boolean tryAdvance(Consumer<? super T> action)的重写
         * */
        @Override
        default boolean tryAdvance(Consumer<? super Long> action){
            if(action instanceof LongConsumer){
                 return tryAdvance((LongConsumer)action);
            }
            else{
                if(Tripwire.ENABLE)
                    Tripwire.trip(getClass(),
                            "{0} calling Spliterator.OfLong.tryAdvance((LongConsumer) action::accept)");
                return tryAdvance((LongConsumer)action::accept);
            }
        }

        /**
         * 这个方法是Spliterator的void forEachRemaining(Consumer<? super T> action)的重写
         * */
        @Override
        default void forEachRemaining(Consumer<? super Long> action){
            if(action instanceof LongConsumer){
                forEachRemaining((LongConsumer) action);
            }
            else{
                if(Tripwire.ENABLE)
                    Tripwire.trip(getClass(),"{0} calling Spliterator.OfLong.forEachRemaining((LongConsumer) action::accept)");
                forEachRemaining((LongConsumer) action::accept);
            }
        }
    }

    /**
     * 继承OfPrimitive接口，并且接受的包装类是Double
     * 对应的消费者为DoubleConsumer
     * */
    public interface OfDouble extends OfPrimitive<Double, DoubleConsumer, OfDouble>{
        @Override
        OfDouble trySplit();

        /**
         * 对OfPrimitive中的boolean tryAdvance(T_CONS action)的重写
         * */
        @Override
        boolean tryAdvance(DoubleConsumer action);

        /**
         * 对OfPrimitive中的default void forEachRemaining(T_CONS action)的重写
         * */
        @Override
        default void forEachRemaining(DoubleConsumer action){do{}while(tryAdvance(action));}

        /**
         * 这个方法是Spliterator的boolean tryAdvance(Consumer<? super T> action)的重写
         * */
        @Override
        default boolean tryAdvance(Consumer<? super Double> action){
            if(action instanceof DoubleConsumer){
                return tryAdvance((DoubleConsumer)action);
            }
            else{
                if(Tripwire.ENABLE)
                    Tripwire.trip(getClass(),
                            "{0} calling Spliterator.OfDouble.tryAdvance((DoubleConsumer) action::accept)");
                return tryAdvance((DoubleConsumer)action::accept);
            }
        }

        /**
         * 这个方法是Spliterator的void forEachRemaining(Consumer<? super T> action)的重写
         * */
        @Override
        default void forEachRemaining(Consumer<? super Double> action){
            if(action instanceof DoubleConsumer){
                forEachRemaining((DoubleConsumer) action);
            }
            else{
                if(Tripwire.ENABLE)
                    Tripwire.trip(getClass(),"{0} calling Spliterator.OfDouble.forEachRemaining((DoubleConsumer) action::accept)");
                forEachRemaining((DoubleConsumer) action::accept);
            }
        }
    }
}