package com.test.util.function;

import java.util.Objects;

@FunctionalInterface
public interface DoublePredicate {

    boolean test(double value);

    default DoublePredicate and(DoublePredicate other){
        Objects.requireNonNull(other);
        return (double v) -> other.test(v) && test(v);
    }

    default DoublePredicate negate(){return (double v) -> !test(v);}

    default DoublePredicate or(DoublePredicate other){
        Objects.requireNonNull(other);
        return (double v) -> other.test(v) || test(v);
    }
}
