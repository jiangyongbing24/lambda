package com.test.util;

import com.test.util.function.IntConsumer;

import java.util.Iterator;
import java.util.Objects;

/**
 * @Created by JYB
 * @Date 2019/7/21 21:32
 * @Description TODO
 */
public interface PrimitiveIterator<T,T_CONS> extends Iterator<T> {
    @SuppressWarnings("overloads")
    void forEachRemaining(T_CONS action);

    public static interface OfInt extends PrimitiveIterator<Integer, IntConsumer>{
        int nextInt();

        default void forEachRemaining(IntConsumer action){
            Objects.requireNonNull(action);
            while(hasNext())
                action.accept(nextInt());
        }

        @Override
        default Integer next(){
            if(Tripwire.ENABLE)
                Tripwire.trip(getClass(),"{0} calling PrimitiveIterator.OfInt.nextInt()");
            return nextInt();
        }
    }
}
