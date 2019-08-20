package com.test.util.stream;

import com.test.util.Spliterator;
import com.test.util.function.IntFunction;

/**
 * 执行 StreamOps 的辅助类，
 * 将Stream每个阶段的所有信息收集到一个地方
 * 输出形状，中间操作，流标志，并行性等
 *
 * PipelineHelper传递给如下方法
 * TerminalOp＃evaluateParallel（PipelineHelper，java.util.Spliterator）
 * TerminalOp＃evaluateSequential（PipelineHelper，java.util.Spliterator）
 * AbstractPipeline＃opEvaluateParallel（PipelineHelper，Spliterator，IntFunction）
 *
 * 可以使用PipelineHelper来访问有关管道的信息，例如头部形状，流标志和大小，
 * 并使用辅助方法，例如wrapAndCopyInto（Sink，Spliterator），copyInto（Sink，Spliterator）
 * 和wrapSink（Sink）来执行管道操作
 * */
abstract class PipelineHelper<P_OUT> {
    /** 获取管道源的流形状 */
    abstract StreamShape getSourceShape();

    /** 获取所描述输出的组合流和操作标志 */
    abstract int getStreamAndOpFlags();

    /**
     * 对于当前的管道阶段，使用spliterator描述的输出部分的确定的大小
     *
     * 如果Spliterator具有已知的SIZED特性，
     * 并且操作标志StreamOpFlag＃SIZED在组合的流和操作标志上是已知的
     * 返回确定的大小
     * */
    abstract<P_IN> long exactOutputSizeIfKnown(Spliterator<P_IN> spliterator);

    /**
     * 使用提供的Spliterator，获取此PipelineHelper描述的管道阶段的元素，
     * 并将结果发送到提供的Sink
     * */
    abstract<P_IN, S extends Sink<P_OUT>> S wrapAndCopyInto(S sink, Spliterator<P_IN> spliterator);

    /**
     * 将从Spliterator获得的元素推送到提供的Sink当中
     * 如果已知流管道中有短路阶段（StreamOpFlag＃SHORT_CIRCUIT），
     * 则在每个元素之后检查{Sink＃cancellationRequested（）}，如果请求取消则停止
     * */
    abstract<P_IN> void copyInto(Sink<P_IN> wrappedSink, Spliterator<P_IN> spliterator);

    /**
     * 将从Spliterator获取的元素推送到提供的Sink，在每个元素后检查Sink＃cancellationRequested（），并在请求取消时停止
     * */
    abstract <P_IN> void copyIntoWithCancel(Sink<P_IN> wrappedSink, Spliterator<P_IN> spliterator);

    /**
     * 接受一个接收PipelineHelper输出类型元素的Sink，并用一个接受输入类型元素的Sink包装它
     * */
    abstract<P_IN> Sink<P_IN> wrapSink(Sink<P_OUT> sink);

    abstract<P_IN> Spliterator<P_OUT> wrapSpliterator(Spliterator<P_IN> spliterator);

    abstract Node.Builder<P_OUT> makeNodeBuilder(long exactSizeIfKnown,
                                                 IntFunction<P_OUT[]> generator);

    /**
     * 从管道中使用提供的源分裂器收集所有的输出元素，并把元素存入Node
     *
     * flatten如果为true且管道是并行管道，则返回的Node将不包含子项，
     * 否则Node可能表示树中反映计算树形状的根
     * */
    abstract<P_IN> Node<P_OUT> evaluate(Spliterator<P_IN> spliterator,
                                        boolean flatten,
                                        IntFunction<P_OUT[]> generator);
}
