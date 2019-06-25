package com.test.util.function;

@FunctionalInterface
public interface DoubleFunction<R> {

    R apply(double value);
}
