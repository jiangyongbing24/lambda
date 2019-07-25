package com.test.util.stream;

import com.test.util.function.LongFunction;
import com.test.util.function.LongPredicate;
import com.test.util.function.LongUnaryOperator;

public interface LongStream extends BaseStream<Long, LongStream> {
    LongStream filter(LongPredicate predicate);

    LongStream map(LongUnaryOperator mapper);

    <U> Stream<U> mapToObj(LongFunction<? extends U> mapper);
}
