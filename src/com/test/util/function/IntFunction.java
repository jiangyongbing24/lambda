package com.test.util.function;

@FunctionalInterface
public interface IntFunction<R> {
    R apply(int value);
}
