package com.test.util;

import java.util.Iterator;

/**
 * 不重复的集合
 */
public interface Set<E> extends Collection<E> {
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
     * 只保留当前集合和集合c的交集
     * */
    boolean retainAll(Collection<?> c);

    /**
     * 移除本集合和集合c的交集
     * */
    boolean removeAll(Collection<?> c);

    /**
     * 清空集合中的元素
     * */
    void clear();

    boolean equals(Object o);

    int hashCode();

    @Override
    default Spliterator<E> getSpliterator(){
        return Spliterators.spliterator(this,Spliterator.DISTINCT);
    }
}
