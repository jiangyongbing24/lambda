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
     * 返回顺序的等效流
     * */
    S sequential();

    /**
     * 返回并行的等效流
     * */
    S parallel();

    /**
     * 返回一个无序的等效流
     * */
    S unordered();

    /**
     * 返回带有附加关闭处理程序的等效流。
     * */
    S onClose(Runnable closeHandler);

    /**
     * 关闭此流和流上管道中的所有的处理程序
     * */
    @Override
    void close();
}
