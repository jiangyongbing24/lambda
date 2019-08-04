package com.test.util.stream;

import com.test.util.Set;
import com.test.util.function.BiConsumer;
import com.test.util.function.BinaryOperator;
import com.test.util.function.Function;
import com.test.util.function.Supplier;

import java.util.Objects;

/**
 * 一个将输入元素累计到可变结果容器当中的抽象接口
 * */
public interface Collector<T,A,R> {
    /**
     * 创建新的结果容器
     * */
    Supplier<A> supplier();

    /**
     * 累加器，将新的数据元素并入结果容器
     * */
    BiConsumer<A,T> accumulator();

    /**
     * 将两个结果容器组合成一个
     * */
    BinaryOperator<A> combiner();

    /**
     * 在容器上执行可选的最终变换
     * */
    Function<A,R> finisher();

    /**
     * 返回搜集器的特征
     * */
    Set<Characteristics> characteristics();

    public static<T,R> Collector<T,R,R> of(Supplier<R> supplier,
                                           BiConsumer<R,T> accumulator,
                                           BinaryOperator<R> combiner,
                                           Characteristics... characteristics){
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(accumulator);
        Objects.requireNonNull(combiner);
        Objects.requireNonNull(characteristics);
        return null;
    }

    enum Characteristics{
        /**
         * 表示此收集器是并发的 ，这意味着结果容器可以支持与多个线程相同的结果容器同时调用的累加器函数。
         * */
        CONCURRENT,

        /**
         * 表示收集操作不承诺保留输入元素的顺序
         * */
        UNORDERED,

        /**
         * 表示整理器功能是身份功能，可以被删除
         * */
        IDENTITY_FINISH
    }
}
