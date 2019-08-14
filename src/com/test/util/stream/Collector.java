package com.test.util.stream;

import com.test.util.Collections;
import com.test.util.EnumSet;
import com.test.util.Set;
import com.test.util.function.BiConsumer;
import com.test.util.function.BinaryOperator;
import com.test.util.function.Function;
import com.test.util.function.Supplier;

import java.util.Objects;

/**
 * 一个将输入元素累计到可变结果容器当中的抽象接口
 *
 * T 累加元素的类型
 * A 结果容器类型
 * R 结果容器转换的类型
 * */
public interface Collector<T,A,R> {
    /**
     * 创建新的可变结果容器
     * */
    Supplier<A> supplier();

    /**
     * 累加器，将新的数据元素并入可变结果容器
     * */
    BiConsumer<A,T> accumulator();

    /**
     * 把容器中的元素复制到当前源中
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

    /** 生成Collector */
    public static<T,R> Collector<T,R,R> of(Supplier<R> supplier,
                                         BiConsumer<R, T> accumulator,
                                         BinaryOperator<R> combiner,
                                         Characteristics... characteristics){
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(accumulator);
        Objects.requireNonNull(combiner);
        Objects.requireNonNull(characteristics);
        //如果特征 characteristics 为0，则返回一个空set
        //否则从 Characteristics.IDENTITY_FINISH 开始，把 characteristics 添加进去
        Set<Characteristics> cs = (characteristics.length == 0)
                ? Collectors.CH_NOID
                : Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.IDENTITY_FINISH,characteristics));
        return new Collectors.CollectorImpl(supplier,accumulator,combiner,cs);
    }

    /** 生成Collector */
    public static<T, A, R> Collector<T, A, R> of(Supplier<A> supplier,
                                                 BiConsumer<A, T> accumulator,
                                                 BinaryOperator<A> combiner,
                                                 Function<A, R> finisher,
                                                 Characteristics... characteristics) {
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(accumulator);
        Objects.requireNonNull(combiner);
        Objects.requireNonNull(finisher);
        Objects.requireNonNull(characteristics);
        //获取一个空Set
        Set<Characteristics> cs = Collectors.CH_NOID;
        if(characteristics.length > 0){
            //使用EnumSet存储Characteristics里面的元素
            cs = EnumSet.noneOf(Characteristics.class);
            //把characteristics里面的元素添加到cs当中
            Collections.addAll(cs,characteristics);
            //把cs转换为一个不能修改的Set
            cs = Collections.unmodifiableSet(cs);
        }
        return new Collectors.CollectorImpl(supplier,accumulator,combiner,finisher,cs);
    }

    /** 描述 Collector 接口的特性 */
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
