package com.test.util.stream;

import com.test.util.Spliterator;
import com.test.util.function.IntFunction;
import com.test.util.function.Supplier;

abstract class ReferencePipeline<P_IN,P_OUT>
        extends AbstractPipeline<P_IN,P_OUT,Stream<P_OUT>>
        implements Stream<P_OUT>{
    ReferencePipeline(Supplier<? extends Spliterator<?>> source,
                      int sourceFlags, boolean parallel) {
        super(source, sourceFlags, parallel);
    }

    ReferencePipeline(Spliterator<?> source,
                      int sourceFlags, boolean parallel) {
        super(source, sourceFlags, parallel);
    }

    ReferencePipeline(AbstractPipeline<?, P_IN, ?> upstream, int opFlags) {
        super(upstream, opFlags);
    }

    /** 获取流的形状 */
    @Override
    final StreamShape getOutputShape() {
        return StreamShape.REFERENCE;
    }
}
