package com.test.util.function;

@FunctionalInterface
public interface LongToIntFunction {
    int applyAsInt(long value);
}
