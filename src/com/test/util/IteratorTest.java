package com.test.util;

import com.test.lang.UnsupportedOperationException;
import com.test.util.function.Consumer;

import java.util.Objects;

public interface IteratorTest<E> {

    boolean hasNext();

    E next();

    default void remove(){throw new UnsupportedOperationException("remove");}

    default void forEachRemaining(Consumer<? super E> action){
        Objects.requireNonNull(action);
        while (hasNext()){ action.accept(next());}}
}
