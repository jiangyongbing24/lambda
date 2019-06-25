package com.test.util.function;

import java.util.Objects;

@FunctionalInterface
public interface IntUnaryOperator {

    int applyAsInt(int operand);

    default IntUnaryOperator compose(IntUnaryOperator before){
        Objects.requireNonNull(before);
        return v -> applyAsInt(before.applyAsInt(v));
    }

    default IntUnaryOperator andThen(IntUnaryOperator after){
        Objects.requireNonNull(after);
        return v -> after.applyAsInt(applyAsInt(v));
    }

    static IntUnaryOperator idetity(){ return t -> t;}
}
