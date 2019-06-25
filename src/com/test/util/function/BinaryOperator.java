package com.test.util.function;

import com.test.util.Comparator;

import java.util.Objects;

@FunctionalInterface
public interface BinaryOperator<T> extends BiFunction<T,T,T> {

    public static <T> BinaryOperator<T> minBy(Comparator<? super T> comparator){
        Objects.requireNonNull(comparator);
        return (T t1,T t2) -> comparator.compare(t1,t2) <= 0 ? t1 : t2;
    }

    public static <T> BinaryOperator<T> maxBy(Comparator<? super T> comparator){
        Objects.requireNonNull(comparator);
        return (T t1,T t2) -> comparator.compare(t1,t2) >= 0 ? t2 : t1;
    }
}
