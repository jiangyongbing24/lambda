package com.test.util.function;

import java.util.Objects;

@FunctionalInterface
public interface Consumer<T> {

    void accept(T t);

    default <V> Consumer<T> andThen(Consumer<? super T> after){
        Objects.requireNonNull(after);
        return (T t) -> {accept(t);after.accept(t);};
    }
}
