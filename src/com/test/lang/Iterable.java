package com.test.lang;

import com.test.util.Spliterator;
import com.test.util.Spliterators;
import com.test.util.function.Consumer;

import java.util.Iterator;
import java.util.Objects;

/**
 * 接口表示一个可迭代的对象
 * */
public interface Iterable<T> extends java.lang.Iterable<T> {
    /**
     * 返回一个迭代器
     * */
    Iterator<T> iterator();

    /**
     * 循环消费元素，只有继承了本接口的才可以使用for(:)循环
     * */
    default void forEach(Consumer<? super T> action) {
        Objects.requireNonNull(action);
        for (T t : this) {
            action.accept(t);
        }
    }

    /**
     * 返回一个分区迭代器
     * */
    default Spliterator<T> getSpliterator() {
        return Spliterators.spliteratorUnknownSize(iterator(), 0);
    }
}
