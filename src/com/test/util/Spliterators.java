package com.test.util;

import com.test.util.function.Consumer;
import com.test.util.function.DoubleConsumer;
import com.test.util.function.IntConsumer;
import com.test.util.function.LongConsumer;

import java.util.Objects;

/**
 * @Created by JYB
 * @Date 2019/7/15 20:03
 * @Description 提供各种分区迭代器
 */
public final class Spliterators {
    private Spliterators(){}

    public static <T> Spliterator<T> emptySpliterator(){return (Spliterator<T>) EMPTY_SPLITERATOR;}

    private static final Spliterator<Object> EMPTY_SPLITERATOR =
            new EmptySpliterator.OfRef<>();

    /**
     * 返回INTEGER类型的空迭代器
     * */
    public static Spliterator.OfInt emptyIntSpliterator(){return (Spliterator.OfInt) EMPTY_INT_SPLITERATOR;}

    private static final Spliterator.OfInt EMPTY_INT_SPLITERATOR =
            new EmptySpliterator.OfInt();

    /**
     * 返回LONG类型的空迭代器
     * */
    private static Spliterator.OfLong emptyLongSpliterator(){return (Spliterator.OfLong) EMPTY_LONG_SPLITERATOR;}

    private static final Spliterator.OfLong EMPTY_LONG_SPLITERATOR =
            new EmptySpliterator.OfLong();

    /**
     * 返回DOUBLE类型的空迭代器
     * */
    public static Spliterator.OfDouble getEmptyDoubleSpliterator(){return (Spliterator.OfDouble) EMPTY_DOUBLE_SPLITERATOR;}

    private static final Spliterator.OfDouble EMPTY_DOUBLE_SPLITERATOR =
            new EmptySpliterator.OfDouble();

    /**
     * 一个抽象的空分区迭代器
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

        public static final class OfRef<T>
                extends EmptySpliterator<T,Spliterator<T>, Consumer<? super T>>
                implements Spliterator<T>{
            OfRef(){}
        }

        public static final class OfInt
                extends EmptySpliterator<Integer, Spliterator.OfInt, IntConsumer>
                implements Spliterator.OfInt{
            OfInt(){}
        }

        public static final class OfLong
                extends EmptySpliterator<Long,Spliterator.OfLong, LongConsumer>
                implements Spliterator.OfLong{
            OfLong(){}
        }

        public static final class OfDouble
                extends EmptySpliterator<Double,Spliterator.OfDouble, DoubleConsumer>
                implements Spliterator.OfDouble{
            OfDouble(){}
        }
    }
}