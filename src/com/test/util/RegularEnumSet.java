package com.test.util;

import com.test.util.function.Consumer;
import test.MyEnum;

import java.util.Iterator;

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

    void addRange(E from, E to) {
        elements = (-1L >>> (from.ordinal() - to.ordinal() - 1) << from.ordinal());
    }

    void addAll() {
        // 根据universe的长度，得到一个值
        // 这个值的二进制的1bit的数量 = universe.length
        // 通过对-1L >>> (64-universe.length)，即可得到符合上述条件的值
        // 而上述公式又等于 -1L >>> -universe.length
        if (universe.length != 0)
            elements = -1L >>> -universe.length;
    }

    void complement() {
        if(universe.length != 0){
            elements = ~elements;
            elements &= -1L >>> -universe.length;
        }
    }

    public Iterator<E> iterator() {
        return null;
    }

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
        Enum<MyEnum>[] myEnum = new Enum[]{MyEnum.one,MyEnum.three};
        RegularEnumSet<MyEnum> set = new RegularEnumSet<MyEnum>(MyEnum.class,myEnum);
//        set.addRange(MyEnum.one,MyEnum.three);
        set.addAll();
        System.out.println(set.size());
    }
}
