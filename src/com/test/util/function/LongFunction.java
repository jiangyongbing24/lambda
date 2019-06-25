package com.test.util.function;

import java.util.Objects;

@FunctionalInterface
public interface LongFunction<R> {

    R apply(long value);
}
