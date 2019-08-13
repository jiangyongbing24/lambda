package com.test.util;

import com.test.lang.IllegalStateException;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * EnumSet的私有实现类，用于特大型枚举类型（即大于64）
 * */
class JumboEnumSet<E extends Enum<E>> extends EnumSet<E> {
    private static final long serialVersionUID = 334349849919042784L;

    /**
     * 该Set的位向量表示，使用long数组表示位向量
     * 当一个不够用的时候，使用多个long类型的作为位向量
     * */
    private long elements[];

    private int size = 0;

    JumboEnumSet(Class<E> elementType, Enum<?>[] universe) {
        super(elementType, universe);
        //初始化一个数组，这个数组的所有bit数大于枚举类型数量
        //使用(universe.length+63)/64只取整数位(等于universe.length >>> 64)即可得到一个正好可以容纳的尺寸
        //只加63而不是64是避免universe.length正好是64的倍数，而数组长度多初始化一个从而浪费资源
        elements = new long[(universe.length + 63) >>> 6];
    }

    void addRange(E from, E to) {
        int fromIndex = from.ordinal() >>> 6;
        int toIndex = to.ordinal() >>> 6;

        //如果两值相等，表示指向他们位置的向量是数组中的同一个元素
        if(fromIndex == toIndex){
            //定位到这个向量，然后初始化向量
            elements[fromIndex] = (-1L >>> (from.ordinal() - to.ordinal() - 1) << from.ordinal());
        }else{
            elements[fromIndex] = (-1L << from.ordinal());//初始化elements[fromIndex]
            for(int i = fromIndex;i < fromIndex - toIndex;i++)
                elements[i] = -1L;
            elements[toIndex] = (-1L >>> -to.ordinal()-1);//初始化elements[toIndex]
        }
        size = to.ordinal() - fromIndex + 1;
    }

    void addAll() {
        for(int i = 0;i < universe.length - 1;i++)
            elements[i] = -1;
        elements[universe.length-1] = -1L >>> -universe.length;
        size = universe.length;
    }

    void complement() {
        for (int i = 0; i < universe.length; i++)
            elements[i] = ~elements[i];
        elements[universe.length-1] &= -1L >>> -universe.length;
        size = universe.length - size;
    }

    public Iterator<E> iterator() {
        return new EnumSetIterator();
    }


    /** 一个用于JumboEnumSet的迭代器 */
    private class EnumSetIterator<E extends Enum<E>> implements Iterator<E>{
        //记录当前elements元素的尚未返回的位向量
        long unseen;

        //尚未迭代到的元素的索引
        int unseenIndex = 0;

        //表示此迭代器返回的最后一个元素的位
        long lastReturned = 0;

        //lastReturned所在elements的索引
        int lastReturnedIndex = 0;

        EnumSetIterator(){unseen=elements[0];}

        public boolean hasNext() {
            //当unseen等于0，并且unseenIndex没有索引到最后一个元素的时候，继续进入循环
            while(unseen == 0 && unseenIndex < elements.length - 1)
                unseen = elements[++unseenIndex];
            return unseen != 0;
        }

        public E next() {
            //如果已经没有元素了，抛出异常
            if(!hasNext())
                throw new NoSuchElementException();
            //获取接下来需要返回的位向量
            lastReturned = unseen & -unseen;
            //记录最后一次返回的索引
            lastReturnedIndex = unseenIndex;
            //去掉已经被迭代的元素
            unseen -= lastReturned;
            return (E)universe[(lastReturnedIndex << 6) + Long.numberOfTrailingZeros(lastReturned)];
        }

        public void remove(){
            if(lastReturned == 0)
                throw new IllegalStateException();
            //记录原来没有修改的元素
            final long oldElements = elements[lastReturnedIndex];
            //修改位向量对应位为0
            elements[lastReturnedIndex] &= ~lastReturned;
            //如果修改成功，大小减一
            if(oldElements != elements[lastReturnedIndex])
                size--;
            lastReturned = 0;
        }
    }

    public int size() {
        return size;
    }
}
