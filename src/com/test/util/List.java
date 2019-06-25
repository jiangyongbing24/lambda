package com.test.util;

import com.test.util.function.UnaryOperator;

import java.util.Iterator;
import java.util.Objects;

public interface List<E> extends Collection<E> {

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

    boolean addAll(int index,Collection<? extends E> c);

    boolean removeAll(Collection<?> c);

    boolean retainAll(Collection<?> c);

    default void replaceAll(UnaryOperator<E> operator){
        Objects.requireNonNull(operator);
        final ListIterator<E> li = this.listIterator();
        while(li.hasNext()){
            li.set(operator.apply(li.next()));
        }
//        for (E e:this) {
//            e = operator.apply(li.next());
//        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    default void sort(Comparator<? extends E> comparator){
        Objects.requireNonNull(comparator);
        for (E e:this) {
        }
    }

    ListIterator<E> listIterator();
}
