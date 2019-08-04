package com.test.util.stream;

import com.test.util.Spliterator;

import java.util.Objects;

/**
 * 用于创建和操纵流的低级实用程序方法
 * */
public final class StreamSupport {
    private StreamSupport(){}

    public static<T> Stream<T> stream(Spliterator<T> spliterator,boolean parallel){
        Objects.requireNonNull(spliterator);
        return null;
    }
}