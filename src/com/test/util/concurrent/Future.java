package com.test.util.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *  提供方法来检查计算是否完成，等待其完成，并检索计算结果
 * */
public interface Future<V> {
    /**
     * 尝试取消此任务代码
     * */
    boolean cancel(boolean mayInterruptIfRunning);

    /**
     * 如果此任务在正常完成之前被取消，返回true
     * */
    boolean isCancelled();

    /**
     *  返回true如果任务已完成。 完成可能是由于正常终止，异常或取消。
     *  在所有这些情况下，此方法将返回true 。
     * */
    boolean isDone();

    /**
     * 等待计算完成，并且检索其结果
     * */
    V get() throws InterruptedException, ExecutionException;

    /**
     * 等待计算完成一段时间，并且检索其结果
     * */
    V get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException;
}
