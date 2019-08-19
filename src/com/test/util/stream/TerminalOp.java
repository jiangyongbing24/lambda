package com.test.util.stream;

import com.test.util.Spliterator;

/**
 * 流管道中的操作，它将流作为输入并生成结果或副作用
 * TerminalOp具有输入类型和流形状以及结果类型
 * TerminalOp还有一组操作标志，用于描述操作如何处理流的元素
 * */
interface TerminalOp<E_IN,R> {
    /** 获取操作的输入类型的形状 */
    default StreamShape inputShape(){return StreamShape.REFERENCE;}

    /** 获取操作的流标志 */
    default int getOpFlags(){return 0;}

    /**
     * 使用指定的操作执行并行操作的评估
     * PipelineHelper描述了上游中间操作
     * */
    default <P_IN> R evaluateParallel(PipelineHelper<E_IN> helper,
                                      Spliterator<P_IN> spliterator) {
        if (Tripwire.ENABLED)
            Tripwire.trip(getClass(), "{0} triggering TerminalOp.evaluateParallel serial default");
        return evaluateSequential(helper, spliterator);
    }

    /**
     * 使用指定的操作执行顺序评估
     * PipelineHelper描述了上游中间操作
     * */
    <P_IN> R evaluateSequential(PipelineHelper<E_IN> helper,
                                Spliterator<P_IN> spliterator);
}
