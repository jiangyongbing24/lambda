package com.test.util.function;

import java.util.Objects;

@FunctionalInterface
public interface IntPredicate {
    boolean test(int value);

    default IntPredicate and(IntPredicate other){
        Objects.requireNonNull(other);
        return (int v) -> other.test(v) && test(v);
    }

    default IntPredicate negate(){return v -> !test(v);}

    default IntPredicate or(IntPredicate other){
        Objects.requireNonNull(other);
        return (int v) -> other.test(v) || test(v);
    }
}
