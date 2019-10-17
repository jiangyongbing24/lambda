package com.test.util.concurrent;

/**
 * 提供一个执行线程的方式 executor(Runnable)
 * */
public interface Executor {
    void executor(Runnable command);
}
