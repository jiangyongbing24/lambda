package com.test.util.stream;

import com.test.util.Spliterator;
import com.test.util.function.BooleanSupplier;
import com.test.util.function.Supplier;

/**
 * 用于包装和委托分裂器的实现，
 * 用于Stream#spliterator()方法的实现
 * */
class StreamSpliterators {
    /**
     * 抽象包装spliterator，在第一次操作时绑定到管道助手的分裂器
     * */
    private static abstract class AbstractWrappingSpliterator<P_IN,P_OUT,T_BUFFER> implements Spliterator<P_OUT> {
        //如果此Spliterator支持拆分，则为true
        final boolean isParallel;

        final PipelineHelper<P_OUT> ph;

        /**
         * 源分裂器的供应商，客户提供分裂器或供应商
         */
        private Supplier<Spliterator<P_IN>> spliteratorSupplier;

        /**
         * 源分裂器，从客户提供或从供应商处获得
         */
        Spliterator<P_IN> spliterator;

        Sink<P_IN> bufferSink;

        BooleanSupplier pusher;

        long nextToConsume;

        T_BUFFER buffer;

        boolean finished;

        AbstractWrappingSpliterator(PipelineHelper<P_OUT> ph,
                                    Supplier<Spliterator<P_IN>> spliteratorSupplier,
                                    boolean parallel) {
            this.ph = ph;
            this.spliteratorSupplier = spliteratorSupplier;
            this.spliterator = null;
            this.isParallel = parallel;
        }

        AbstractWrappingSpliterator(PipelineHelper<P_OUT> ph,
                                    Spliterator<P_IN> spliterator,
                                    boolean parallel) {
            this.ph = ph;
            this.spliteratorSupplier = null;
            this.spliterator = spliterator;
            this.isParallel = parallel;
        }

        //如果需要，在推进设置分裂器之前调用
        final void init(){
            if(spliterator == null){
                spliterator = spliteratorSupplier.get();
                spliteratorSupplier = null;
            }
        }
    }
}
