package com.test.util.stream;

import com.test.util.Spliterator;

/**
 * 执行 StreamOps 的辅助类
 * 输出形状，中间操作，流标志，并行性等
 * */
abstract class PipelineHelper<P_OUT> {
    /** 获取管道源的流形状 */
    abstract StreamShape getSourceShape();

    abstract int getStreamAndOpFlags();

    abstract<P_IN> long exactOutputSizeIfKnown(Spliterator<P_IN> spliterator);

    abstract<P_IN, S extends Sink<P_OUT>> S wrapAndCopyInto(S sink, Spliterator<P_IN> spliterator);

    abstract<P_IN> void copyInto(Sink<P_IN> wrappedSink, Spliterator<P_IN> spliterator);

    abstract <P_IN> void copyIntoWithCancel(Sink<P_IN> wrappedSink, Spliterator<P_IN> spliterator);

    abstract<P_IN> Sink<P_IN> wrapSink(Sink<P_OUT> sink);

    abstract<P_IN> Spliterator<P_OUT> wrapSpliterator(Spliterator<P_IN> spliterator);
}
