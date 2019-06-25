package com.test.util;

import com.test.util.function.Predicate;

import java.util.Iterator;
import java.util.Objects;

public interface Collection<E> extends Iterable<E> {

    int size();

    boolean isEmpty();

    boolean contains(Object obj);

    Iterator<E> iterator();

    Object[] toArray();

    <T> T[] toArray(T[] a);

    boolean add(E e);

    boolean remove(Object o);

    boolean containsAll(Collection<?> c);

    boolean addAll(Collection<? extends E> c);

    boolean removeAll(Collection<?> c);

    default boolean removeIf(Predicate<E> filter){
        Objects.requireNonNull(filter);
        boolean removed = false;
        final Iterator<E> iterator = iterator();
        while (iterator.hasNext()){
            if(filter.test(iterator.next())){
                iterator.remove();
                removed = true;
            }
        }
        return removed;
    }

    boolean retainAll(Collection<?> c);

    void clear();

    boolean equals(Object o);

    int hashCode();
}
