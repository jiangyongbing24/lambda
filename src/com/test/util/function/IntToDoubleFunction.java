package com.test.util.function;

import java.util.Objects;

@FunctionalInterface
public interface IntToDoubleFunction {
    double applyAsDouble(int value);
}
