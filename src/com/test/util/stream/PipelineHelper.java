package com.test.util.stream;

import com.test.util.Spliterator;
import com.test.util.function.IntFunction;

/**
 * 执行 StreamOps 的辅助类，将Stream的所有信息收集到一个地方
 * 输出形状，中间操作，流标志，并行性等
 * */
abstract class PipelineHelper<P_OUT> {
    /** 获取管道源的流形状 */
    abstract StreamShape getSourceShape();

    /** 获取所描述输出的组合流和操作标志 */
    abstract int getStreamAndOpFlags();

    abstract<P_IN> long exactOutputSizeIfKnown(Spliterator<P_IN> spliterator);

    /** 将此PipelineHelper描述的管道阶段应用于提供的Spliterator，并将结果发送到提供的Sink */
    abstract<P_IN, S extends Sink<P_OUT>> S wrapAndCopyInto(S sink, Spliterator<P_IN> spliterator);

    abstract<P_IN> void copyInto(Sink<P_IN> wrappedSink, Spliterator<P_IN> spliterator);

    abstract <P_IN> void copyIntoWithCancel(Sink<P_IN> wrappedSink, Spliterator<P_IN> spliterator);

    abstract<P_IN> Sink<P_IN> wrapSink(Sink<P_OUT> sink);

    abstract<P_IN> Spliterator<P_OUT> wrapSpliterator(Spliterator<P_IN> spliterator);

    abstract Node.Builder<P_OUT> makeNodeBuilder(long exactSizeIfKnown,
                                                 IntFunction<P_OUT[]> generator);

    abstract<P_IN> Node<P_OUT> evaluate(Spliterator<P_IN> spliterator,
                                        boolean flatten,
                                        IntFunction<P_OUT[]> generator);
}
