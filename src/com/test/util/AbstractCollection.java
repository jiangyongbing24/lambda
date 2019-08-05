package com.test.util;

import java.util.Iterator;

/**
 * 抽象集合
 **/
public abstract class AbstractCollection<E> implements Collection<E> {
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    protected AbstractCollection(){}

    public abstract Iterator<E> iterator();

    public abstract int size();

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean contains(Object o) {
        Iterator<E> it = iterator();
        if (o==null) {
            while (it.hasNext())
                if (it.next()==null)
                    return true;
        } else {
            while (it.hasNext())
                if (o.equals(it.next()))
                    return true;
        }
        return false;
    }

    public Object[] toArray() {
        Object[] r = new Object[size()];//根据尺寸创建一个数组
        Iterator<E> it = iterator();
        for (int i = 0; i < r.length; i++) {
            if (! it.hasNext())//如果当前集合中没有元素
                return Arrays.copyOf(r, i);//把r中的数组复制到一个新的数组返回
            r[i] = it.next();
        }
        //如果迭代器还没结束，交给 finishToArray 处理
        return it.hasNext() ? finishToArray(r, it) : r;
    }

    /** 迭代器超出预期 */
    private static <T> T[] finishToArray(T[] r, Iterator<?> it) {
        int i = r.length;//记录原来的数组长度
        while (it.hasNext()) {
            int cap = r.length;//cap记录当前r的容量
            if (i == cap) {//当i还是不足够容纳所有的元素的时候，继续扩展r
                int newCap = cap + (cap >> 1) + 1;//扩充容量
                // 意识到溢出的元素
                if (newCap - MAX_ARRAY_SIZE > 0)
                    newCap = hugeCapacity(cap + 1);
                r = Arrays.copyOf(r, newCap);//把当前r里面的数据复制进入新的数组
            }
            r[i++] = (T)it.next();//i自增
        }
        //如果最终i等于r的长度，直接返回，否则去掉数组r中的空元素
        return (i == r.length) ? r : Arrays.copyOf(r, i);
    }

    /** 处理巨大容量的数组 */
    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError
                    ("Required array size too large");
        return (minCapacity > MAX_ARRAY_SIZE) ?
                Integer.MAX_VALUE :
                MAX_ARRAY_SIZE;
    }
}
