package com.test.util;

import sun.misc.SharedSecrets;

import java.util.Iterator;

/**
 * 一个专门Set实现与枚举类型一起使用
 */
public abstract class EnumSet<E extends Enum<E>>
        extends AbstractSet<E>
        implements Cloneable, java.io.Serializable
{
    /** 集合中所有元素的类型 */
    final Class<E> elementType;

    /** 存储 EnumSet 中的所有元素 */
    final Enum<?>[] universe;

    private static Enum<?>[] ZERO_LENGTH_ENUM_ARRAY = new Enum<?>[0];

    EnumSet(Class<E> elementType, Enum<?>[] universe) {
        this.elementType = elementType;
        this.universe = universe;
    }

    /** 使用指定的枚举类型创建一个空的 EnumSet */
    public static <E extends Enum<E>> EnumSet<E> noneOf(Class<E> elementType) {
        Enum<?>[] universe = getUniverse(elementType);//获取所有elementType类型的枚举值
        if (universe == null)
            throw new ClassCastException(elementType + " not an enum");//如果不是枚举类型抛出异常

        if (universe.length <= 64)
            return new RegularEnumSet<>(elementType, universe);
        else
            return new JumboEnumSet<>(elementType, universe);
    }

    /** 创建一个包含指定枚举类型中所有元素的 EnumSet */
    public static <E extends Enum<E>> EnumSet<E> allOf(Class<E> elementType) {
        EnumSet<E> result = noneOf(elementType);
        result.addAll();//把当前的枚举类型的值添加到这个空的EnumSet当中
        return result;
    }

    /** 将适当的枚举类型中的所有元素添加到此枚举中，在调用之前为null */
    abstract void addAll();

    /** 返回s的一个副本 */
    public static <E extends Enum<E>> EnumSet<E> copyOf(EnumSet<E> s){return s.clone();}

    /** 返回集合s的一个副本 */
    public static <E extends Enum<E>> EnumSet<E> copyOf(Collection<E> c) {
        if (c instanceof EnumSet) {
            //如果是EnumSet类型，直接返回一个副本
            return ((EnumSet<E>)c).clone();
        } else {
            if (c.isEmpty())
                throw new IllegalArgumentException("Collection is empty");
            Iterator<E> i = c.iterator();
            E first = i.next();
            EnumSet<E> result = EnumSet.of(first);//根据第一个元素创建一个 EnumSet
            //使用迭代器一个个复制
            while (i.hasNext())
                result.add(i.next());
            return result;
        }
    }

    /** 返回s的一个补码，如果 s存储了E类型枚举的所有枚举值，返回null，否则返回s没有存储到的元素*/
    public static <E extends Enum<E>> EnumSet<E> complementOf(EnumSet<E> s) {
        EnumSet<E> result = copyOf(s);
        result.complement();
        return result;
    }

    /** 创建一个只含 e 元素的 EnumSet */
    public static <E extends Enum<E>> EnumSet<E> of(E e) {
        EnumSet<E> result = noneOf(e.getDeclaringClass());
        result.add(e);
        return result;
    }

    /** 创建一个含有 e1,e2 元素的 EnumSet */
    public static <E extends Enum<E>> EnumSet<E> of(E e1, E e2) {
        EnumSet<E> result = noneOf(e1.getDeclaringClass());
        result.add(e1);
        result.add(e2);
        return result;
    }

    /** 创建一个含有 e1,e2,e3 元素的 EnumSet */
    public static <E extends Enum<E>> EnumSet<E> of(E e1, E e2, E e3) {
        EnumSet<E> result = noneOf(e1.getDeclaringClass());
        result.add(e1);
        result.add(e2);
        result.add(e3);
        return result;
    }

    /** 创建一个含有 e1,e2,e3,e4 元素的 EnumSet */
    public static <E extends Enum<E>> EnumSet<E> of(E e1, E e2, E e3, E e4) {
        EnumSet<E> result = noneOf(e1.getDeclaringClass());
        result.add(e1);
        result.add(e2);
        result.add(e3);
        result.add(e4);
        return result;
    }

    /** 创建一个含有 e1,e2,e3,e4,e5 元素的 EnumSet */
    public static <E extends Enum<E>> EnumSet<E> of(E e1, E e2, E e3, E e4,
                                                    E e5)
    {
        EnumSet<E> result = noneOf(e1.getDeclaringClass());
        result.add(e1);
        result.add(e2);
        result.add(e3);
        result.add(e4);
        result.add(e5);
        return result;
    }

    /** 创建一个含有以 first 作为第一个元素，rest为后续的 EnumSet */
    @SafeVarargs
    public static <E extends Enum<E>> EnumSet<E> of(E first, E... rest) {
        EnumSet<E> result = noneOf(first.getDeclaringClass());
        result.add(first);
        for (E e : rest)
            result.add(e);
        return result;
    }

    /** 获取from至to区间的枚举值，并且组成一个EnumSet */
    public static <E extends Enum<E>> EnumSet<E> range(E from, E to) {
        if (from.compareTo(to) > 0)
            throw new IllegalArgumentException(from + " > " + to);
        EnumSet<E> result = noneOf(from.getDeclaringClass());
        result.addRange(from, to);
        return result;
    }

    /** 转为此EnumSet的from到to区间 */
    abstract void addRange(E from, E to);

    @SuppressWarnings("unchecked")
    public EnumSet<E> clone() {
        try {
            return (EnumSet<E>) super.clone();
        } catch(CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    /** 转为EnumSet的补码 */
    abstract void complement();

    /**
     * 如果e不是枚举类型，抛出异常
     */
    final void typeCheck(E e) {
        Class<?> eClass = e.getClass();
        if (eClass != elementType && eClass.getSuperclass() != elementType)
            throw new ClassCastException(eClass + " != " + elementType);
    }

    /**
     * 返回包含E的所有值
     * 结果不能克隆，不能缓存，不能共享
     */
    private static <E extends Enum<E>> E[] getUniverse(Class<E> elementType) {
        //返回jvm里面存在的elementType类型的所有值
        return SharedSecrets.getJavaLangAccess()
                .getEnumConstantsShared(elementType);
    }

    /** EnumSet的代理序列化类 */
    private static class SerializationProxy <E extends Enum<E>>
            implements java.io.Serializable
    {
        //元素的枚举类型
        private final Class<E> elementType;

        //EnumSet中包含的所有元素
        private final Enum<?>[] elements;

        SerializationProxy(EnumSet<E> set) {
            elementType = set.elementType;
            elements = set.toArray(ZERO_LENGTH_ENUM_ARRAY);
        }

        @SuppressWarnings("unchecked")
        private Object readResolve() {
            EnumSet<E> result = EnumSet.noneOf(elementType);
            for (Enum<?> e : elements)
                result.add((E)e);
            return result;
        }

        private static final long serialVersionUID = 362491234563181265L;
    }

    Object writeReplace(){return new SerializationProxy<>(this);}

    //序列化代理模式的readObject方法
    private void readObject(java.io.ObjectInputStream stream)
            throws java.io.InvalidObjectException {
        throw new java.io.InvalidObjectException("Proxy required");
    }
}
