package com.test.util.function;

import java.util.Objects;

@FunctionalInterface
public interface LongPredicate {

    boolean test(long value);

    default LongPredicate and(LongPredicate other){
        Objects.requireNonNull(other);
        return v -> other.test(v) && test(v);
    }

    default LongPredicate negate(){return t -> !test(t);}

    default LongPredicate or(LongPredicate other){
        Objects.requireNonNull(other);
        return v -> other.test(v) || test(v);
    }
}
