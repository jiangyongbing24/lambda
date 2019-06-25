package com.test.util.function;

@FunctionalInterface
public interface DoubleToLongFunction {
    long applyAsLong(double value);
}
