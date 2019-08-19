package com.test.util.stream;

import com.test.util.Spliterator;
import com.test.util.function.IntFunction;
import com.test.util.function.Supplier;

/**
 * “管道”类的抽象基类，它是Stream接口的核心实现及其原始特化，管理流管道的建设和评估
 *
 * AbstractPipeline表示流管道的初始部分，封装流源和零个或多个中间操作
 * 各个AbstractPipeline对象通常称为阶段，其中每个阶段描述流源或中间操作
 * 特定形状的类 会将处理结果集合的辅助方法 添加到适当的 特定形状的容器中
 *
 * 链接新的中间操作或执行一个终端操作，流被认为是消耗的，
 * 并且不会再有可能执行中间操作或者终端操作
 *
 * 对于顺序流和并行流，如果所有的中间操作都是无状态的，
 * 那么将会把所有操作“阻塞”在一起，然后管道评估会在单个管道中间进行
 * 对于具体有状态的并行流，执行被分成段，其中每个有状态操作标记段的结束，
 * 并且每个段被单独评估并且结果用作下一段的输入
 *
 * 在所有情况下，在终端操作开始之前不会消耗源数据
 * */
abstract class AbstractPipeline<E_IN, E_OUT, S extends BaseStream<E_OUT, S>>
        extends PipelineHelper<E_OUT> implements BaseStream<E_OUT, S>  {
    private static final String MSG_STREAM_LINKED = "stream has already been operated upon or closed";
    private static final String MSG_CONSUMED = "source already consumed or closed";

    /** 反向链接到管道头部，如果这是源阶段，则是自己 */
    private final AbstractPipeline sourceStage;

    /** “上游”管道，如果这是源阶段，则为null */
    private final AbstractPipeline previousStage;

    /** 此管道对象表示的中间操作的操作标志 */
    protected final int sourceOrOpFlags;

    /**
     * 管道中的下一个阶段，如果是最后一个阶段的话则为null
     * 在链接到下一个管道的时候有效
     * */
    @SuppressWarnings("rawtypes")
    private AbstractPipeline nextStage;

    /**
     * 此管道对象与流源（如果是顺序）之间的中间操作数，或之前的状态（如果并行）
     * 在管道准备评估的时候有效
     * */
    private int depth;

    /** 结合所有的操作标志，在管道准备评估的时候有效 */
    private int combinedFlags;

    /**
     * 一个仅对头管道有效的分区迭代器
     * 在管道消费之前，如果sourceSpliterator非null的话，那么sourceSupplier必须为null
     * 如果不是null，在使用管道之后把其设置为null
     * */
    private Spliterator<?> sourceSpliterator;

    /**
     * 流的来源供应商，仅对头管道有效
     * 在管道消费之前，sourceSupplier不为null，则必须设置为null
     * 在管道消费之后，sourceSupplier不为null，则将其设置为null
     * */
    private Supplier<? extends Spliterator<?>> sourceSupplier;

    /** 如果此管道已经被连接或者消费，则为true */
    private boolean linkedOrConsumed;

    /** 如果管道中有任何有状态的操作，则为true，只对原阶段有效 */
    private boolean sourceAnyStateful;

    private Runnable sourceCloseAction;

    /** 如果管道是并行的，则为true，否则管道是顺序的，仅对原阶段有效 */
    private boolean parallel;

    /** 初始化的源管道 */
    AbstractPipeline(Supplier<? extends Spliterator<?>> source,
                     int sourceFlags,boolean parallel){
        this.previousStage = null;
        this.sourceSupplier = source;
        this.sourceStage = this;
        //根据Stream的掩码，保留对应的特征
        this.sourceOrOpFlags = sourceFlags & StreamOpFlag.STREAM_MASK;
        //假如sourceOrOpFlags是连续的01，那么~(sourceOrOpFlags << 1)会等于它本身
        //否则表示会把两个位区间的中间区间的位全部设置为1
        //比如 0b01000001，这个数分成4个位区间，interval[4] = {01,00,00,01}，
        //但是interval[0]到interval[3]不是连续的01，所以中间需要使用1bit来填充
        //0b01000001变为0b01111101，即interval[4] = {01,11,11,01}
        this.combinedFlags = (~(sourceOrOpFlags << 1)) & StreamOpFlag.INITIAL_OPS_VALUE;
        this.depth = 0;
        this.parallel = parallel;
    }

    /** 初始化的源管道 */
    AbstractPipeline(Spliterator<?> source,
                     int sourceFlags,boolean parallel){
        this.previousStage = null;
        this.sourceSpliterator = source;
        this.sourceStage = this;
        //根据Stream的掩码，保留对应的特征
        this.sourceOrOpFlags = sourceFlags & StreamOpFlag.STREAM_MASK;
        this.combinedFlags = (~(sourceOrOpFlags << 1)) & StreamOpFlag.INITIAL_OPS_VALUE;
        this.depth = 0;
        this.parallel = parallel;
    }

    /** 初始化中间管道 */
    AbstractPipeline(AbstractPipeline<?,E_IN,?> previousStage,int opFlags){
        //如果此管道已经被连接或者消费，抛出异常
        if(previousStage.linkedOrConsumed)
            throw new IllegalStateException();
        previousStage.linkedOrConsumed = true;
        previousStage.nextStage = this;

        this.previousStage = previousStage;
        this.sourceOrOpFlags = opFlags & StreamOpFlag.OP_MASK;
        this.combinedFlags = StreamOpFlag.combineOpFlags(opFlags,previousStage.combinedFlags);
        this.sourceStage = previousStage.sourceStage;
        // 如果是有状态的操作
        if(opIsStateful())
            sourceStage.sourceAnyStateful = true;
        this.depth = previousStage.depth + 1;
    }

    /** 使用终端操作评估管道以生成结果 */
    final <R> R evaluate(TerminalOp<E_OUT,R> terminalOp){
        //管到的输出形状等于终端执行操作的形状
        assert getOutputShape() == terminalOp.inputShape();
        if(linkedOrConsumed)
            throw new IllegalStateException();
        linkedOrConsumed = true;

        return isParallel()
                ? terminalOp.evaluateParallel(this, sourceSpliterator(terminalOp.getOpFlags()))
                : terminalOp.evaluateSequential(this, sourceSpliterator(terminalOp.getOpFlags()));
    }

    /**
     * 获取此管道阶段的源spliterator
     * 对于顺序的和无状态的并行管道，这是源分裂器
     * 对于有状态的并行管道，这是一个分裂器，
     * 用于描述所有的计算结果，包括最近的有状态操作
     * */
    @SuppressWarnings("unchecked")
    private Spliterator<?> sourceSpliterator(int terminalFlags){
        Spliterator<?> spliterator = null;
        // 获取源管道的分裂器
        if(sourceStage.sourceSpliterator != null){
            spliterator = sourceStage.sourceSpliterator;
            sourceStage.sourceSpliterator = null;
        }
        else if(sourceStage.sourceSupplier != null){
            // 根据分裂器供应商提供分离器
            spliterator = (Spliterator<?>) sourceStage.sourceSupplier.get();
            sourceStage.sourceSupplier = null;
        }
        else{
            throw new IllegalStateException(MSG_CONSUMED);
        }

        //如果是并行的，并且源阶段含有有状态的操作
        if(isParallel() && sourceStage.sourceAnyStateful){
            //深度置为1
            int depth = 1;
            //迭代管道
            for(@SuppressWarnings("rawtypes") AbstractPipeline u = sourceStage,p = sourceStage.nextStage,e = this;
                u != e;u = p,p = p.nextStage){
                int thisOpFlags = p.sourceOrOpFlags;//获取当前标志
                if(p.opIsStateful()){//如果是有状态的操作
                    depth = 0;//重置迭代的深度
                    //如果含有短路操作
                    if(StreamOpFlag.SHORT_CIRCUIT.isKnown(thisOpFlags)){
                        //清除下一个流水线阶段的短路标志
                        //这个阶段封装了短路操作，下一个阶段可能没有任何短路操作
                        //如果是这样，spliterator.forEachRemaining应该用于遍历
                        thisOpFlags = thisOpFlags & ~StreamOpFlag.IS_SHORT_CIRCUIT;
                    }
                    spliterator = p.opEvaluateParallelLazy(u,spliterator);

//                    thisOpFlags = spliterator.hasCharacteristics(Spliterator.SIZED) ? :;
                }
            }
        }
        return null;
    }

    @Override
    public final boolean isParallel() {
        return sourceStage.parallel;
    }

    /**
     * 获取管道的输出形状
     * 如果管道是头部，然后它的输出形状对应于源的形状。
     * 否则，它的输出形状对应于输出形状相关操作
     * */
    abstract StreamShape getOutputShape();

    /**
     * 返回此操作是否有状态，如果它是有状态的，必须重写方法
     * opEvaluateParallel（PipelineHelper，java.util.Spliterator，java.util.function.IntFunction）
     * */
    abstract boolean opIsStateful();

    /**
     * 用指定的操作执行并行操作的评估
     * PipelineHelper描述了上游的中间操作
     * 只调用有状态操作。如果opIsStateful（）返回true，则必须覆盖默认实现
     * 返回一个描述评估结果的Node
     * */
    <P_IN> Node<E_OUT> opEvaluateParallel(PipelineHelper<E_OUT> helper,
                                          Spliterator<P_IN> spliterator,
                                          IntFunction<E_OUT[]> generator) {
        throw new UnsupportedOperationException("Parallel evaluation is not supported");
    }

    /**
     * 用指定的操作执行并行操作的评估
     * PipelineHelper描述了上游的中间操作
     * 只调用有状态操作。如果opIsStateful（）返回true，则必须覆盖默认实现
     * 返回一个描述评估结果的Spliterator
     * */
    @SuppressWarnings("unchecked")
    <P_IN> Spliterator<E_OUT> opEvaluateParallelLazy(PipelineHelper<E_OUT> helper,
                                                     Spliterator<P_IN> spliterator) {
        return opEvaluateParallel(helper, spliterator, i -> (E_OUT[]) new Object[i]).getSpliterator();
    }
}
