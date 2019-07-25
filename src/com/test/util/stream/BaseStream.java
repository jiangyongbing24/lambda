package com.test.util.stream;

import com.test.util.Spliterator;

import java.util.Iterator;

public interface BaseStream<T,S extends BaseStream<T,S>> extends AutoCloseable {
    Iterator<T> iterator();

    Spliterator<T> spliterator();

    /**
     * 是平行的
     * */
    boolean isPrallel();

    /**
     * 顺序的
     * */
    S sequential();

    /**
     * 无序的
     * */
    S unordered();

    S onClose(Runnable closeHandler);

    @Override
    void close();
}
