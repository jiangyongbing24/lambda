package com.test.util;

import com.test.util.function.Consumer;
import test.MyEnum;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * EnumSet的私有实现类，用于"常规大小"的枚举型（即小于等于64）
 *
 * 对于这个类的位运算符做一个介绍：
 * 对于Java的>>>,>>,<<位运算符号，如果这些运算符的左边为int，则运算符的右边只有低5位有效，如果运算符的左边为long，则运算符的右边只有低6位有效
 * 举例：int a = 123456789; 则 a >> 0b10101 == a >> 0b110101 为true，因为只有低5位10101会被算作有效的。
 * 换个说法就是：位运算符会以 运算符的左边的数据类型在内存中存储的bit数 作为一个运算周期
 * 比如说int在内存中是使用32bit存储的，那么运算符以32作为周期，上述的 a >> 0b10101 == a >> 0b110101 转换位十进制为 a >> 21 == a >> 53，其中21和53差了一个32周期
 * 通过上述的描述，我们能理解的是，当运算符右边为一个小于0的数的时候，它可以转换为对应的整数然后进行运算，这个整数减去负数之差除以32的余数要为0
 * */
class RegularEnumSet<E extends Enum<E>> extends EnumSet<E> {
    private static final long serialVersionUID = 3411599620347842686L;

    /**
     * 该集的位向量表示
     *
     * 比如现有一个枚举:enum MyEnum{one,two,three;}
     * RegularEnumSet存储的就是此枚举值，那么位向量为0x7，转换为二进制为00...0111
     * 从二进制的最右边开始，1bit代表对应的枚举的位置，
     * 即如果RegularEnumSet含有MyEnum.two，则elements二进制的 MyEnum.two.origin() 位一定为1，其他的枚举值类似
     * 当进行了 addRange(MyEnum.one,MyEnum.two)，elements变为00...0110，再进行 complement(),
     * elements变为00...0001
     * */
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
        //再对这个数进行from.ordinal()的左位移，向量最左边的1bit的2的幂值等于from.ordinal()
        //只有实例化RegularEnumSet的时候，universe包含全部的枚举，elements向量才能正确的指向
        elements = (-1L >>> (from.ordinal() - to.ordinal() - 1) << from.ordinal());
    }

    /**
     * 把universe中的元素正式添加进来，
     * 在没有进行这个动作之前，RegularEnumSet是个空set
     * */
    void addAll() {
        // 根据universe的长度，得到一个符合下述条件的值:
        // 这个值的二进制的1bit的数量 == universe.length
        //
        // 通过对-1L >>> (64-universe.length)，即可得到符合上述条件的值
        // 而上述公式又等于 -1L >>> -universe.length，为什么等于查看12行的java位运算符号的介绍
        if (universe.length != 0)
            elements = -1L >>> -universe.length;
    }

    /**
     * 转为EnumSet的补码
     * 在进行这一步之前，确定进行了addAll或者addRange操作
     * */
    void complement() {
        if(universe.length != 0){
            //取elements的补码
            elements = ~elements;
            //用这个补码和 初始的位向量(-1L >>> -universe.length) 进行与运算
            //因为 初始的位向量 对应枚举的位置全为1，与现有的位向量的补码进行与，
            //得到一个原来位向量的一个补码位向量
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
        //还没迭代的元素的位向量
        long unseen;

        //表示迭代器最后一个元素的位
        long lastReturned = 0;

        EnumSetIterator(){unseen = elements;}

        public boolean hasNext() {
            return unseen != 0;
        }

        @SuppressWarnings("unchecked")
        public E next() {
            if(unseen == 0)
                throw new NoSuchElementException();
            //lastReturned指向最低位的1bit
            lastReturned = unseen & -unseen;
            //unseen去除最后一个元素的位向量
            unseen -= lastReturned;
            //返回对应位置的元素
            return (E) universe[Long.numberOfTrailingZeros(lastReturned)];
        }

        public void remove(){
            if(lastReturned == 0)
                throw new IllegalStateException();
            //移除对应位置上的位向量
            elements &= ~lastReturned;
            //lastReturned重置
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
        //如果类型对应不上，直接返回false
        if(eClass != elementType || eClass.getSuperclass() != elementType)
            return false;

        //使用1左移e.ordinal()位，这个数字的二进制除了e.ordinal()位为1bit，其余均为0bit
        //使用elements进行与运算之后，如果elements的第e.ordinal()位为1，则结果不会为0，表示包含元素e
        return (elements & (1L << ((Enum<?>)e).ordinal())) != 0;
    }

    public boolean add(E e){
        //如果e不是枚举类型，抛出异常
        typeCheck(e);

        long oldElements = elements;
        //对应位置的位改为1
        elements |= 1L << ((Enum<?>)e).ordinal();
        return elements != oldElements;
    }

    public boolean remove(Object e){
        if(e == null)
            return false;
        Class<?> eClass = e.getClass();
        //如果类型对应不上，直接返回false
        if(eClass != elementType || eClass.getSuperclass() != elementType)
            return false;

        long oldElements = elements;
        //对应位置的位向量改为0
        elements &= ~(1L << ((Enum<?>)e).ordinal());
        return elements != oldElements;
    }

    public boolean containsAll(Collection<?> c){
        if(!(c instanceof RegularEnumSet))
            return super.containsAll(c);

        RegularEnumSet<?> es = (RegularEnumSet<?>) c;
        if(es.elementType != elementType)
            return es.isEmpty();//如果类型不相等，但是为空，返回true

        //如果位向量相等，返回true
        return (es.elements & ~elements) == 0;
    }

    public boolean addAll(Collection<? extends E> c){
        if(!(c instanceof RegularEnumSet))
            return super.addAll(c);

        RegularEnumSet<?> es = (RegularEnumSet<?>) c;
        if(es.elementType != elementType){
            if(isEmpty())//如果类型不相等，但是为空，返回true
                return true;
            else
                throw new ClassCastException(es.elementType + " != " + elementType);
        }

        long oldElements = elements;
        elements |= es.elements;
        return elements != oldElements;
    }

    public boolean removeAll(Collection<?> c){
        if(!(c instanceof RegularEnumSet))
            return super.removeAll(c);

        RegularEnumSet<?> es = (RegularEnumSet<?>) c;
        if(es.elementType != elementType)
            return false;

        long oldElements = elements;
        elements &= ~es.elements;
        return elements != oldElements;
    }

    public boolean retainAll(Collection<?> c) {
        if (!(c instanceof RegularEnumSet))
            return super.retainAll(c);

        RegularEnumSet<?> es = (RegularEnumSet<?>)c;
        if (es.elementType != elementType) {
            boolean changed = (elements != 0);
            elements = 0;
            return changed;
        }

        long oldElements = elements;
        elements &= es.elements;
        return elements != oldElements;
    }

    public void clear() {
        elements = 0;
    }

    public boolean equals(Object o) {
        if (!(o instanceof RegularEnumSet))
            return super.equals(o);

        RegularEnumSet<?> es = (RegularEnumSet<?>)o;
        if (es.elementType != elementType)
            return elements == 0 && es.elements == 0;
        return es.elements == elements;
    }

    public static void main(String[] args) {
        Enum<MyEnum>[] myEnum = new Enum[]{MyEnum.one,MyEnum.two,MyEnum.three};
        RegularEnumSet<MyEnum> set = new RegularEnumSet<MyEnum>(MyEnum.class,myEnum);
        System.out.println(Long.toBinaryString(set.elements));
        set.addAll();
        set.addRange(MyEnum.two,MyEnum.one);
        System.out.println(Long.toBinaryString(set.elements));
        set.complement();
        System.out.println(Long.toBinaryString(set.elements));
    }
}
