package com.test.util.function;

import java.util.Objects;

@FunctionalInterface
public interface LongConsumer {
    void accept(long value);

    default LongConsumer andThen(LongConsumer after){
        Objects.requireNonNull(after);
        return v -> {accept(v);after.accept(v);};
    }
}
