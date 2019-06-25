package com.test.util.function;

import java.util.Objects;

@FunctionalInterface
public interface DoubleConsumer {

    void accept(double value);

    default DoubleConsumer andThen(DoubleConsumer after){
        Objects.requireNonNull(after);
        return (v) -> {after.accept(v);accept(v);};
    }
}
