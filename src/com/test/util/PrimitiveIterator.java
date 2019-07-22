package com.test.util;

import com.test.util.function.DoubleConsumer;
import com.test.util.function.IntConsumer;
import com.test.util.function.LongConsumer;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @Created by JYB
 * @Date 2019/7/21 21:32
 * @Description 基本类型的包装类的迭代器接口
 */
public interface PrimitiveIterator<T,T_CONS> extends Iterator<T> {
    @SuppressWarnings("overloads")
    void forEachRemaining(T_CONS action);

    /**
     * 整数类型的迭代器接口
     * */
    public static interface OfInt extends PrimitiveIterator<Integer, IntConsumer>{
        int nextInt();

        default void forEachRemaining(IntConsumer action){
            Objects.requireNonNull(action);
            while(hasNext())
                action.accept(nextInt());
        }

        @Override
        default Integer next(){
            if(Tripwire.ENABLE)
                Tripwire.trip(getClass(),"{0} calling PrimitiveIterator.OfInt.nextInt()");
            return nextInt();
        }

        @Override
        default void forEachRemaining(Consumer<? super Integer> action){
            if(action instanceof IntConsumer){
                forEachRemaining((IntConsumer)action);
            }
            else{
                Objects.requireNonNull(action);
                if(Tripwire.ENABLE)
                    Tripwire.trip(getClass(),"{0} calling PrimitiveIterator.OfInt.forEachRemainingInt(action::accept)");
                forEachRemaining((IntConsumer) action::accept);
            }
        }
    }

    /**
     * 长整数类型的迭代器接口
     * */
    public static interface OfLong extends PrimitiveIterator<Long, LongConsumer>{
        long nextLong();

        default void forEachRemaining(LongConsumer action){
            Objects.requireNonNull(action);
            while(hasNext())
                action.accept(nextLong());
        }

        @Override
        default Long next(){
            if(Tripwire.ENABLE)
                Tripwire.trip(getClass(),"{0} calling PrimitiveIterator.OfLong.nextLong()");
            return nextLong();
        }

        @Override
        default void forEachRemaining(Consumer<? super Long> action){
            if(action instanceof LongConsumer){
                forEachRemaining((LongConsumer)action);
            }
            else{
                Objects.requireNonNull(action);
                if(Tripwire.ENABLE)
                    Tripwire.trip(getClass(),"{0} calling PrimitiveIterator.OfLong.forEachRemainingInt(action::accept)");
                forEachRemaining((LongConsumer) action::accept);
            }
        }
    }

    /**
     * 浮点数类型的迭代器接口
     * */
    public static interface OfDouble extends PrimitiveIterator<Double, DoubleConsumer>{
        double nextDouble();

        default void forEachRemaining(DoubleConsumer action){
            Objects.requireNonNull(action);
            while(hasNext())
                action.accept(nextDouble());
        }

        @Override
        default Double next(){
            if(Tripwire.ENABLE)
                Tripwire.trip(getClass(),"{0} calling PrimitiveIterator.OfDouble.nextDouble()");
            return nextDouble();
        }

        @Override
        default void forEachRemaining(Consumer<? super Double> action){
            if(action instanceof DoubleConsumer){
                forEachRemaining((DoubleConsumer)action);
            }
            else{
                Objects.requireNonNull(action);
                if(Tripwire.ENABLE)
                    Tripwire.trip(getClass(),"{0} calling PrimitiveIterator.OfDouble.forEachRemainingInt(action::accept)");
                forEachRemaining((DoubleConsumer) action::accept);
            }
        }
    }
}
