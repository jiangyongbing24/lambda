package com.test.util.stream;

import com.test.util.function.Consumer;
import com.test.util.function.DoubleConsumer;
import com.test.util.function.IntConsumer;
import com.test.util.function.LongConsumer;

import java.util.Objects;

/**
 * 一个接收器用来协调相邻Stage之间的调用关系
 *
 * 每个Stage都会将自己的操作封装到一个Sink里，
 * 前一个Stage只需调用后一个Stage的accept()方法即可，并不需要知道其内部是如何处理的
 * */
interface Sink<T> extends Consumer<T> {
    /**
     * 开始遍历元素之前调用该方法，通知Sink做好准备
     * 当然对于有状态的操作，方法必须实现
     * */
    default void begin(long size){}

    /**
     * 所有元素遍历完成之后调用，通知Sink没有更多的元素了
     * 当然对于有状态的操作，才方法必须实现
     * */
    default void end(){}

    /**
     * 是否可以结束操作，可以让短路操作尽早结束
     * */
    default boolean cancellationRequested(){return false;}

    /**
     * 遍历元素时调用，接受一个待处理元素，并对元素进行处理
     * Stage把自己包含的操作和回调方法封装到该方法里，前一个Stage只需要调用当前Stage.accept(T t)方法就行了
     * */
    default void accept(int value){throw new IllegalStateException("called wrong accept method");}

    default void accept(long value){throw new IllegalStateException("called wrong accept method");}

    default void accept(double value){throw new IllegalStateException("called wrong accept method");}

    /**
     * 只接受整数的Sink
     * */
    interface OfInt extends Sink<Integer>, IntConsumer {
        @Override
        void accept(int value);

        @Override
        default void accept(Integer i){
            if(Tripwire.ENABLED)
                Tripwire.trip(getClass(),"{0} calling Sink.OfInt.accept(Integer)");
            accept(i.intValue());
        }
    }

    /**
     * 只接受长整数的Sink
     * */
    interface OfLong extends Sink<Long>, LongConsumer {
        @Override
        void accept(long value);

        @Override
        default void accept(Long i){
            if(Tripwire.ENABLED)
                Tripwire.trip(getClass(),"{0} calling Sink.LongConsumer.accept(Long)");
            accept(i.longValue());
        }
    }

    /**
     * 只接受浮点数的Sink
     * */
    interface OfDouble extends Sink<Double>, DoubleConsumer {
        @Override
        void accept(double value);

        @Override
        default void accept(Double i){
            if(Tripwire.ENABLED)
                Tripwire.trip(getClass(),"{0} calling Sink.DoubleConsumer.accept(Double)");
            accept(i.doubleValue());
        }
    }

    /**
     * 一个链式调用的抽象类
     * */
    static abstract class ChainedReference<T,E_OUT> implements Sink<T>{
        //下一个Sink
        protected final Sink<? super E_OUT> downstream;

        //使用下一个Sink初始化当前Sink，当前Sink有了下一个链接
        public ChainedReference(Sink<? super E_OUT> downstream){
            this.downstream = Objects.requireNonNull(downstream);
        }

        @Override
        public void begin(long size){downstream.begin(size);}

        @Override
        public void end(){downstream.end();}

        @Override
        public boolean cancellationRequested(){return downstream.cancellationRequested();}
    }

    /**
     * 一个整数链式调用的抽象类
     * */
    static abstract class ChainedInt<E_OUT> implements Sink.OfInt{
        protected final Sink<? super E_OUT> downstream;

        public ChainedInt(Sink<? super E_OUT> downstream){
            this.downstream = Objects.requireNonNull(downstream);
        }

        @Override
        public void begin(long size){downstream.begin(size);}

        @Override
        public void end(){downstream.end();}

        @Override
        public boolean cancellationRequested(){return downstream.cancellationRequested();}
    }

    /**
     * 一个长整数链式调用的抽象类
     * */
    static abstract class ChainedLong<E_OUT> implements Sink.OfLong{
        protected final Sink<? super E_OUT> downstream;

        public ChainedLong(Sink<? super E_OUT> downstream){
            this.downstream = Objects.requireNonNull(downstream);
        }

        @Override
        public void begin(long size){downstream.begin(size);}

        @Override
        public void end(){downstream.end();}

        @Override
        public boolean cancellationRequested(){return downstream.cancellationRequested();}
    }

    /**
     * 一个浮点数链式调用的抽象类
     * */
    static abstract class ChainedDouble<E_OUT> implements Sink.OfDouble{
        protected final Sink<? super E_OUT> downstream;

        public ChainedDouble(Sink<? super E_OUT> downstream){
            this.downstream = Objects.requireNonNull(downstream);
        }

        @Override
        public void begin(long size){downstream.begin(size);}

        @Override
        public void end(){downstream.end();}

        @Override
        public boolean cancellationRequested(){return downstream.cancellationRequested();}
    }
}
