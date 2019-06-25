package com.test.util.function;

import java.util.Objects;

@FunctionalInterface
public interface Predicate<T> {

    boolean test(T t);

    default Predicate<T> and(Predicate<? super T> other){
        Objects.requireNonNull(other);
        return t -> other.test(t) && test(t);
    }

    default Predicate<T> negate(){
        return t -> !test(t);
    }

    default Predicate<T> or(Predicate<? super T> other){
        Objects.requireNonNull(other);
        return t -> other.test(t) || test(t);
    }

    static <T> Predicate<T> isEqual(Object targetRef){
        return (null == targetRef) ? Objects::isNull : obj -> targetRef.equals(obj);
    }
}
