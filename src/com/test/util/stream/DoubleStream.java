package com.test.util.stream;

import com.test.util.function.DoubleFunction;
import com.test.util.function.DoublePredicate;
import com.test.util.function.DoubleUnaryOperator;

public interface DoubleStream extends BaseStream<Double, DoubleStream> {
    DoubleStream filter(DoublePredicate predicate);

    DoubleStream map(DoubleUnaryOperator mapper);

    <U> Stream<U> mapToObj(DoubleFunction<? extends U> mapper);
}
