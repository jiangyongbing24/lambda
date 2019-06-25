package com.test.util;

import java.util.Iterator;

public interface ListIterator<E> extends Iterator<E> {

    boolean hasNext();

    E next();

    boolean hasPrevious();

    E previous();

    int nextIndex();

    int previousIndex();

    void remove();

    void set(E e);

    void add(E e);
}
