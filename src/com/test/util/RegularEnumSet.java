package com.test.util;

import com.test.util.function.Consumer;
import test.MyEnum;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * EnumSet的私有实现类，用于"常规大小"的枚举型（即小于等于64）
 * */
class RegularEnumSet<E extends Enum<E>> extends EnumSet<E> {
    private static final long serialVersionUID = 3411599620347842686L;

    //该集的位向量表示
    private long elements = 0L;

    RegularEnumSet(Class<E> elementType, Enum<?>[] universe) {
        super(elementType, universe);
    }

    /**
     * 把from到to之间的枚举添加进来
     * */
    void addRange(E from, E to) {
        //from.ordinal()-to.ordinal()-1得到区间[from,to]的元素数量的相反数
        //-1L不带符号右移这个相反数会得到二进制有对应数量1bit的数
        //再对这个数进行左位移
        elements = (-1L >>> (from.ordinal() - to.ordinal() - 1) << from.ordinal());
    }

    /**
     * 把universe中的元素正式添加进来，
     * 在没有进行这个动作之前，RegularEnumSet是个空set
     * */
    void addAll() {
        // 根据universe的长度，得到一个值
        // 这个值的二进制的1bit的数量 = universe.length
        // 通过对-1L >>> (64-universe.length)，即可得到符合上述条件的值
        // 而上述公式又等于 -1L >>> -universe.length
        if (universe.length != 0)
            elements = -1L >>> -universe.length;
    }

    /** 转为EnumSet的补码 */
    void complement() {
        if(universe.length != 0){
            elements = ~elements;
            elements &= -1L >>> -universe.length;
        }
    }

    public Iterator<E> iterator() {
        return new EnumSetIterator();
    }

    /**
     * 一个用来迭代 EnumSet 的迭代器
     * */
    private class EnumSetIterator<E extends Enum<E>> implements Iterator<E>{
        long unseen;

        long lastReturned = 0;

        EnumSetIterator(){unseen = elements;}

        public boolean hasNext() {
            return unseen != 0;
        }

        @SuppressWarnings("unchecked")
        public E next() {
            if(unseen == 0)
                throw new NoSuchElementException();
            lastReturned = unseen & -unseen;
            unseen -= lastReturned;
            return (E) universe[Long.numberOfTrailingZeros(lastReturned)];
        }

        public void remove(){
            if(lastReturned == 0)
                throw new IllegalStateException();
            elements &= ~lastReturned;
            lastReturned = 0;
        }
    }

    //RegularEnumSet的大小为elements含有1bit的数量
    public int size() {return Long.bitCount(elements);}

    public boolean isEmpty(){return elements == 0;}

    public boolean contains(Object e){
        if(e == null)
            return false;
        Class<?> eClass = e.getClass();
        if(eClass != elementType || eClass.getSuperclass() != elementType)
            return false;

        return (elements & (1L << ((Enum<?>)e).ordinal())) != 0;
    }

    public static void main(String[] args) {
        Enum<MyEnum>[] myEnum = new Enum[]{MyEnum.one,MyEnum.two};
        RegularEnumSet<MyEnum> set = new RegularEnumSet<MyEnum>(MyEnum.class,myEnum);
        System.out.println(Long.toBinaryString(set.elements));
        set.addAll();
//        set.addRange();
//        System.out.println(Long.toBinaryString(set.elements));
        set.complement();
        System.out.println(Long.toBinaryString(set.elements));
//        System.out.println(set.size());
    }
}
