package com.test.util.stream;

import com.test.util.Collection;
import com.test.util.Comparator;
import com.test.util.Optional;
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

    /**
     * 返回一个新的不重复的流
     * */
    Stream<T> distinct();

    /**
     * 返回一个具有自然顺序序列的流，如果流不具有序列，抛出ClassCastException异常
     * */
    Stream<T> sorted();

    /**
     * 根据特定的比较器返回一个具有序列的流
     * */
    Stream<T> sorted(Comparator<? super T> comparator);

    /**
     * 返回该流元素所组成的流，再组成新流的时候消费元素一次
     * */
    Stream<T> peek(Consumer<? super T> action);

    /**
     * 从流开始位置截取maxSize数量的元素，并使用这些元素组成新流
     * */
    Stream<T> limit(long maxSize);

    /**
     * 从流开始位置丢弃n个元素，剩下的元素组成新流
     * */
    Stream<T> skip(long n);

    /**
     * 循环消费流元素
     * */
    void forEach(Consumer<? super T> action);

    /**
     * 如果流定义的了顺序，则以流的定义的顺序对该流的每个元素执行操作。
     * */
    void forEachOrdered(Consumer<? super T> action);

    /**
     * 把流转换成数组
     * */
    Object[] toArray();

    /**
     * 使用generator函数把流转换成数组
     * */
    <A> A[] toArray(IntFunction<A[]> generator);

    /**
     * 从流中第一个元素t0开始，执行v0 = accumulator(identity,t0)，
     * 得到返回值v0与下一个元素t1继续执行v1 = accumulator(v0,t1)，直至流中元素全部进行了操作
     *
     * 在并行流情况下，accumulator必须是一个符合结合律的操作，也就是accumulator不依赖执行的顺序，任何执行顺序最后的结果都一样
     * 结合律的操作示例：加法，最小和最大值以及字符串连接
     *
     * accumulator -一个 associative ， non-interfering ， stateless功能组合两个值
     * */
    T reduce(T identity,BinaryOperator<T> accumulator);

    /**
     * 从流中第一个和第二个元素t0,t1开始，执行v0 = accumulator(t0,t1)，
     * 得到返回值v0与下一个元素t2继续执行v1 = accumulator(v0,t2)，直至流中元素全部进行了操作，最终返回值包装成Optional类
     *
     * 在并行流情况下，accumulator必须是一个符合结合律的操作，也就是accumulator不依赖执行的顺序，任何执行顺序最后的结果都一样
     * 结合律的操作示例：加法，最小和最大值以及字符串连接
     *
     * accumulator -一个 associative ， non-interfering ， stateless功能组合两个值
     * */
    Optional<T> reduce(BinaryOperator<T> accumulator);

    /**
     * 在串行流的情况下，combiner无作用，此计算式等同于 U reduce(U identity,BiFunction<U,? super T,U> accumulator);
     * 其中accumulator必须符合结合律
     *
     * 在并行流的情况下，combiner除了符合结合律，还必须符合如下公式
     * combiner.apply(u,accumulator.apply(accumulator,t)) = accumulator.apply(u,t)
     *
     * accumulator -一个 associative ， non-interfering ， stateless功能组合两个值
     * combiner -一个 associative ， non-interfering ， stateless功能组合两个值
     * */
    <U> U reduce(U identity,BiFunction<U,? super T,U> accumulator,BinaryOperator<U> combiner);

    /**
     * 将输入元素累加到可变结果容器中，例如Collection或StringBuilder ，使用它处理流中的元素
     * supplier提供一个容器，accumulator会把元素添加到容器当中，combiner会用于组合两个值
     *
     * 在串行流当中，combiner无作用
     * 在并行流的情况下，combiner符合结合律
     *
     * @param supplier 一个提供可变容器的供应商
     * @param accumulator 一个叠加器，把源内元素叠加到容器当中
     * @param combiner 把容器中的元素复制到当前源中
     * @return a stream with a handler that is run if the stream is closed
     * */
    <R> R collect(Supplier<R> supplier,BiConsumer<R,? super T> accumulator,BiConsumer<R,R> combiner);

    /**
     * 通过一个搜集器完成Stream的mutable reduction操作
     * */
    <R,A> R collect(Collector<? super T,A,R> collector);

    /**
     * 通过comparator比较器返回流中最小的元素，返回这个最小元素初始化的Optional类
     * */
    Optional<T> min(Comparator<? super T> comparator);

    /**
     * 通过comparator比较器返回流中最大的元素，返回这个最大元素初始化的Optional类
     * */
    Optional<T> max(Comparator<? super T> comparator);

    /**
     * 统计流中元素数量
     * */
    long count();

    /** 流中是否有元素匹配谓词predicate */
    boolean anyMatch(Predicate<? super T> predicate);

    /** 流中所有元素是否都匹配谓词predicate */
    boolean allMatch(Predicate<? super T> predicate);

    /** 没有元素符合谓词predicate，返回true，否则返回false */
    boolean noneMatch(Predicate<? super T> predicate);

    /** 返回第一个元素 */
    Optional<T> findFirst();

    /** 返回任意的一个元素 */
    Optional<T> findAny();

    public static<T> Builder<T> build(){return null;}

    /**
     * Stream的可变构造器，通过一个一个接受元素生成Stream
     * 没有使用复制开销来作为临时缓冲区
     * */
    public interface Builder<T> extends Consumer<T>{
        /** 向正在构建的流添加元素 */
        @Override
        void accept(T t);

        /** 向正在构建的流添加元素 */
        default Builder<T> add(T t){
            accept(t);
            return this;
        }

        /**
         * 构建Stream，并且把Stream的状态修改为已经构建
         * */
        Stream<T> build();
    }
}
