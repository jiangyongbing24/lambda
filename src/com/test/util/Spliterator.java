package com.test.util;

import com.test.util.function.Consumer;

public interface Spliterator<T> {

    boolean tryAdvance(Consumer<? super T> action);

    default void forEachRemaining(Consumer<T> action){
        do{}while(tryAdvance(action));
    }
}
