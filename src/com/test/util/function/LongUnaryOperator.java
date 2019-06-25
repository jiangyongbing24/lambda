package com.test.util.function;

import java.util.Objects;

@FunctionalInterface
public interface LongUnaryOperator {
    long applyAsLong(long value);

    default LongUnaryOperator compose(LongUnaryOperator before){
        Objects.requireNonNull(before);
        return v -> applyAsLong(before.applyAsLong(v));
    }

    default LongUnaryOperator andThen(LongUnaryOperator after){
        Objects.requireNonNull(after);
        return v -> after.applyAsLong(applyAsLong(v));
    }

    static LongUnaryOperator identity(){return t -> t;}
}
