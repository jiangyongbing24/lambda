package com.test.util;

import com.test.util.function.Predicate;
import com.test.util.stream.Collector;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;

public class Collections {
    private Collections(){}

    private static final int BINARYSEARCH_THRESHOLD   = 5000;
    private static final int REVERSE_THRESHOLD        =   18;
    private static final int SHUFFLE_THRESHOLD        =    5;
    private static final int FILL_THRESHOLD           =   25;
    private static final int ROTATE_THRESHOLD         =  100;
    private static final int COPY_THRESHOLD           =   10;
    private static final int REPLACEALL_THRESHOLD     =   11;
    private static final int INDEXOFSUBLIST_THRESHOLD =   35;

    static final Set<Collector.Characteristics> CH_NOID = Collections.emptySet();

    public static final Set EMPTY_SET = new EmptySet<>();

    /**
     * 返回一个可以序列化的 EmptySet
     */
    @SuppressWarnings("unchecked")
    public static final <T> Set<T> emptySet() {
        return (Set<T>) EMPTY_SET;
    }

    /**表示一个空Set*/
    private static class EmptySet<E>
            extends AbstractSet<E>
            implements Serializable
    {
        private static final long serialVersionUID = 1582296315990362920L;

        public Iterator<E> iterator(){return emptyIterator();}

        public int size() {return 0;}
        public boolean isEmpty() {return true;}

        public boolean contains(Object obj) {return false;}
        public boolean containsAll(Collection<?> c) { return c.isEmpty(); }

        public Object[] toArray() { return new Object[0]; }

        public <T> T[] toArray(T[] a) {
            if (a.length > 0)
                a[0] = null;
            return a;
        }

        // Override default methods in Collection
        @Override
        public void forEach(Consumer<? super E> action) {
            Objects.requireNonNull(action);
        }
        @Override
        public boolean removeIf(Predicate<? super E> filter) {
            Objects.requireNonNull(filter);
            return false;
        }
        @Override
        public Spliterator<E> getSpliterator() { return Spliterators.emptySpliterator(); }

        // Preserves singleton property
        private Object readResolve() {
            return EMPTY_SET;
        }
    }

    /** 提供一个空的迭代器 */
    @SuppressWarnings("unchecked")
    public static <T> Iterator<T> emptyIterator() {
        return (Iterator<T>) EmptyIterator.EMPTY_ITERATOR;
    }

    /** 一个空的迭代器 */
    private static class EmptyIterator<E> implements Iterator<E> {
        static final EmptyIterator<Object> EMPTY_ITERATOR
                = new EmptyIterator<>();

        public boolean hasNext() { return false; }
        public E next() { throw new NoSuchElementException(); }
        public void remove() { throw new IllegalStateException(); }
        @Override
        public void forEachRemaining(Consumer<? super E> action) {
            Objects.requireNonNull(action);
        }
    }
}
