package com.test.util.stream;

import com.test.util.function.*;

public interface Stream<T> extends BaseStream<T,Stream<T>> {
    /**
     * 筛选元素
     * */
    Stream<T> filter(Predicate<? super T> predicate);

    /**
     * 把当前Stream<T>流转换为Stream<R>流
     *
     * function把一个T类型的元素经过一些中间操作，转换为R类型的元素，
     * 然后把这个R类型的元素转交给map处理，
     * 最后map把这些R类型的元素重新生成一个Stream<R>流
     * */
    <R> Stream<R> map(Function<? super T,? extends R> function);

    /**
     * 把当前Stream<T>流转换为IntStream流
     * */
    IntStream mapToInt(ToIntFunction<? super T> mapper);

    /**
     * 把当前Stream<T>流转换为LongStream流
     * */
    LongStream mapToLong(ToLongFunction<? super T> mapper);

    /**
     * 把当前Stream<T>流转换为DoubleStream流
     * */
    DoubleStream mapToDouble(ToDoubleFunction<? super Double> mapper);

    /**
     * 把Stream<T>流转换为Stream<R>流
     *
     * 此方法与 Stream<R> map 的区别为：
     * flatMap会把Stream<T>转换为多个Stream<R>流，然后把这些Stream<R>流聚合成一个新的Stream<R>流
     * */
    <R> Stream<R> flatMap(Function<? super T,? extends Stream<R>> mapper);

    /**
     * 把Stream<T>流转换为IntStream流
     * */
    IntStream flatMapToInt(Function<? super T,? extends IntStream> mapper);

    /**
     * 把Stream<T>流转换为LongStream流
     * */
    LongStream flatMaoToLong(Function<? super T,? extends LongStream> mapper);

    /**
     * 把Stream<T>流转换为DoubleStream流
     * */
    DoubleStream flatToDouble(Function<? super T,? extends DoubleStream> mapper);
}
