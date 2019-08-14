package com.test.util;

import com.test.util.function.Predicate;

import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.Objects;
import com.test.lang.Iterable;
import com.test.util.stream.Stream;

public interface Collection<E> extends Iterable<E> {
    /**
     * 返回集合的大小
     * */
    int size();

    /**
     * 判断集合是否为空
     * */
    boolean isEmpty();

    /**
     * 判断是否含有某个元素
     * */
    boolean contains(Object obj);

    /**
     * 得到一个迭代器
     * */
    Iterator<E> iterator();

    /**
     * 把集合转换为Object数组
     * */
    Object[] toArray();

    /**
     * 把集合转换为T类型的数组
     * */
    <T> T[] toArray(T[] a);

    /**
     * 往集合中添加一个元素
     * */
    boolean add(E e);

    /**
     * 移除集合中的一个元素
     * */
    boolean remove(Object o);

    /**
     * 是否含有某个集合中的全部元素
     * */
    boolean containsAll(Collection<?> c);

    /**
     * 把另一个集合中的所有元素添加到本集合中
     * */
    boolean addAll(Collection<? extends E> c);

    /**
     * 移除本集合和集合c的交集
     * */
    boolean removeAll(Collection<?> c);

    /**
     * 移除集合中满足表达式filter的元素，如果有元素被移除，返回true，否则返回false
     * */
    default boolean removeIf(Predicate<? super E> filter){
        Objects.requireNonNull(filter);
        boolean result = false;
        final Iterator<E> it = iterator();
        while(it.hasNext()){
            if(filter.test(it.next())){
                it.remove();
                result = true;
            }
        }
        return result;
    }

    /**
     * 只保留当前集合和集合c的交集
     * */
    boolean retainAll(Collection<?> c);

    /**
     * 清空集合中的元素
     * */
    void clear();

    boolean equals(Object o);

    int hashCode();

    /**
     * 返回一个分区迭代器
     * */
    @Override
    default Spliterator<E> getSpliterator(){
        return Spliterators.spliterator(this,0);
    }

    default Stream<E> stream() {
        return null;
    }

    default Stream<E> parallelStream() {
        return null;
    }
}
