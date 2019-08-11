package com.test.util;

import java.util.Iterator;

class JumboEnumSet<E extends Enum<E>> extends EnumSet<E> {
    private static final long serialVersionUID = 334349849919042784L;

    JumboEnumSet(Class<E> elementType, Enum<?>[] universe) {
        super(elementType, universe);
    }

    @Override
    void addAll() {

    }

    @Override
    void addRange(E from, E to) {

    }

    @Override
    void complement() {

    }

    @Override
    public Iterator<E> iterator() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }
}
