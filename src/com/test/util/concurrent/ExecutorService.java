package com.test.util.concurrent;

import com.test.util.List;

import java.util.concurrent.TimeUnit;

/**
 * Executor的管理者
 * */
public interface ExecutorService extends Executor {
    /**
     * 关闭任务接受，但是已经提交了的任务会继续执行
     * */
    void shutDown();

    /**
     * 尝试停止所有正在执行的任务，并且返回所有未执行的任务
     * */
    List<Runnable> shutDownNow();

    /**
     * ExecutorService是否已经关闭
     * */
    boolean isShutDown();

    /**
     * 判断所有已经提交的任务是否已经完成
     * */
    boolean isTerminated();

    /**
     * 阻塞ExecutorService，等待指定时间，
     * 如果时间到了，任务没有执行完毕，
     * 抛出InterruptedException
     * */
    boolean awaitTermination(long timeout, TimeUnit unit)
            throws InterruptedException;
}
