package com.test.util;

import java.util.Iterator;

public interface Collection<E> extends Iterable<E> {

    int size();

    boolean isEmpty();

    boolean contains(Object obj);

    Iterator<E> iterator();

    Object[] toArray();

    <T> T[] toArray(T[] a);
}
