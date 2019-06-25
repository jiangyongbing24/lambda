package com.test.util.function;

@FunctionalInterface
public interface ObjIntConsumer<T> {
    void accept(T t,int value);
}
