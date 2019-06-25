package com.test.util.function;

@FunctionalInterface
public interface ToDoubleFunction<T> {
    double applyAsDouble(T t);
}
