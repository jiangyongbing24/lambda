package com.test.util.function;

@FunctionalInterface
public interface ToLongFunction<T> {
    long applyAsLong(T t);
}
