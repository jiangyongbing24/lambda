package com.test.util;

import java.io.Serializable;
import java.util.Objects;

public class HashMap<K,V> extends AbstractMap<K,V> implements Cloneable, Serializable {
    private static final long serialVersionUID = 362498820763181265L;

    /** 初始容量，必须是2的幂等 */
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;

    /**
     * 如果具有参数的任一构造函数隐式指定更高的值，则使用最大容量。必须是2的幂<= 1 << 30
     * */
    static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * 在构造函数中未指定时使用的加载因子
     */
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * 列表转Tree的阙值
     */
    static final int TREEIFY_THRESHOLD = 8;

    /** 当前HashMap的元素数量 */
    transient int size;

    /** HashMap的存储单位，继承Map.Entry */
    static class Node<K,V> implements Map.Entry<K,V>{
        final int hash;
        final K key;
        V value;
        Node<K,V> next;

        Node(int hash,K key,V value,Node<K,V> next){
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public final K getKey() {return key;}
        public final V getValue() {return value;}
        public final String toString(){return key+"="+value;}

        public final int hashCode(){return Objects.hashCode(key) ^ Objects.hashCode(value);}

        public V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

        public boolean equals(Object o){
            if(this == o)
                return true;
            if(o instanceof Map.Entry){
                Map.Entry<?,?> e = (Map.Entry<?,?>)o;
                if(Objects.equals(getKey(),e.getKey()) &&
                        Objects.equals(getValue(),e.getValue()))
                    return true;
            }
            return false;
        }
    }

    /**
     * 返回key的一个哈希值，这是一个"扰动函数"
     *
     * 这里为什么不直接使用key.hashCode()作为这个哈希值呢?
     * 每个Node都会对应table中的一个索引，为了让Node的hash值跟索引对应，有如下运算：
     * 即有 int n = table.size(); int index = (n - 1) & hash;
     * 因为n只能是2的幂，所以n-1得到的是一个全为1的掩码(n = 16;n-1 == 15 == 0b1111)，
     * 这个掩码跟任何值与运算，都只会保留它的部分低位，
     * 虽然key.hashCode()从-2147483648到2147483648上具有大概40亿的映射空间，
     * 但是为了让这个哈希值能映射到table当中，会把它跟table.size()-1进行与运算，
     * 只保留它不超过table.size()的部分，这会大大增加碰撞概率，
     * 使用如下运算，混合原始哈希码的高位和低位，以此来加大低位的随机性
     * */
    static final int hash(Object key){
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    /**
     * 返回一个2的幂的值作为容量，这个容量能最小开销化的存储cap数量的元素
     * 比如说，当cap为15的时候，那么容量就应该定位 16=2^4，当cap为17的时候，
     * 容量就只能定为 32=2^5
     * */
    static final int tableSizeFor(int cap) {
        return cap < 0
                ? 1
                : cap > MAXIMUM_CAPACITY
                ? MAXIMUM_CAPACITY
                : 1 << Integer.SIZE - Integer.numberOfLeadingZeros(cap-1);
    }

    transient Node<K,V>[] table;

    /** 此HashMap结构被修改的次数 */
    transient int modCount;

    int threshold;

    /**
     * 加载因子，即使用空间达到总空间的加载因子时，需要扩容。
     */
    final float loadFactor;

    /**
     * 使用指定的初始容量构造一个空的HashMap
     * 容量和负载系数
     * */
    public HashMap(int initialCapacity, float loadFactor){
        if(initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " +
                    initialCapacity);
        //如果超出最大容量，容量定位最大容量
        if(initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        if(loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " +
                    loadFactor);
        this.loadFactor = loadFactor;
        //初始化容量，这个容量正好能储存initialCapacity，并且是2的幂等
        this.threshold = tableSizeFor(initialCapacity);
    }

    /**
     * 使用指定的初始容量构造一个空的HashMap
     * */
    public HashMap(int initialCapacity){this(initialCapacity,DEFAULT_LOAD_FACTOR);}

    public HashMap(){
        this.loadFactor = DEFAULT_LOAD_FACTOR;
    }

    /**
     * 使用一个Map初始化HashMap
     *
     * 这里jdk源码存在一个bug，如下：
     * HashMap<Integer,Integer> map1 = new HashMap<>();
     * for(int i = 1;i <= 12;i++){
     *      map1.put(i,i);
     * }
     * HashMap<Integer,Integer> map2 = new HashMap<>(map1);
     * 在这里，map1中table.size()=16，而map2采用错误的table.size()=32来存储12个元素，
     * 这造成了存储浪费
     * */
    public HashMap(Map<? extends K,? extends V> m){
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        putMapEntries(m,false);
    }

    /**
     * 把m中间所有的元素添加进当前的HashMap
     */
    final void putMapEntries(Map<? extends K, ? extends V> m, boolean evict) {
        // 获取m的大小
        int s = m.size();
        if (s > 0) {
            if (table == null) {
                // 计算至少需要多少容量
                float ft = ((float)s / loadFactor) + 1.0F;
                // 如果大于最大的容量则设为最大的容量
                int t = ((ft < (float)MAXIMUM_CAPACITY) ?
                        (int)ft : MAXIMUM_CAPACITY);
                // 如果大于当前的实际存储空间
                if (t > threshold)
                    threshold = tableSizeFor(t);
            }
            else if (s > threshold)
                resize();
            // 循环读取m，然后把值添加到当前的HashMap当中
            for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
                K key = e.getKey();
                V value = e.getValue();
                putVal(hash(key), key, value, false, evict);
            }
        }
    }

    /** 返回当前存储的元素的数量 */
    public int size() {
        return size;
    }

    /** 当前HashMap是否为空 */
    public boolean isEmpty() {
        return size == 0;
    }

    /** 根据key获取值 */
    public V get(Object key){
        Node<K,V> e;
        return (e = getNode(hash(key), key)) == null ? null : e.value;
    }

    /** 根据hash值和key得到Node */
    final Node<K,V> getNode(int hash,Object key){return null;}

    /** 判断是否含有键值为key的元素 */
    public boolean containsKey(Object key) {
        return getNode(hash(key), key) != null;
    }

    /** 往HashMap中添加键值对 */
    public V put(K key,V value){return putVal(hash(key),key,value,false,true);}

    /**
     * 实现Map.put的相关方法
     *
     * @param hash key的哈希值
     * @param key 键
     * @param value 键值
     * @param onlyIfAbsent 如果为true，则不改变已经存在的值
     * @param evict 如果是false，table处于创建模式
     * @return 上一个值，如果没有，返回null
     */
    final V putVal(int hash,K key,V value,boolean onlyIfAbsent,boolean evict){
        Node<K,V>[] tab;Node<K,V> p;int n,i;
        // 如果table为null，或者大小为0
        if((tab = table) == null || (n = tab.length) == 0)
            n = (tab = resize()).length;//初始化或加倍表格大小
        // 如果索引处没有存储元素
        if((p = tab[i = (n - 1) & hash]) == null)
            tab[i] = newNode(hash,key,value,null);//添加元素
        // 如果索引处已经存储了元素
        else{
            Node<K,V> e;K k;
            // 如果hash相同，并且key相同，表示同一个元素
            // 把这个值赋给e
            if(p.hash == hash &&
                    ((k = p.key) == key || (k != null && k.equals(key))))
                e = p;
            // 如果p已经为TreeNode的数据结构
            else if(p instanceof TreeNode)
                e = ((TreeNode)p).putTreeVal(this,tab,hash,key,value);
            // 如果p不是TreeNode的数据结构
            else{
                // 定位到链表的最后一个节点
                for(int bitCount = 0;;++bitCount){
                    // e指向下一个元素，然后判断是否到达Node链表中的最后一个节点
                    if((e = p.next) == null){
                        // 到达了Node链表中的最后一个元素，创建一个Node节点
                        // 并且让p.next指向这个新创建的Node节点
                        p.next = newNode(hash, key, value, null);
                        // 如果达到了bin的索引的阙值
                        if(bitCount >= TREEIFY_THRESHOLD - 1)
                            treeifyBin(tab, hash);// 根据hash值，把对应索引上的链表结构转换为TreeNode的数据结构
                        break;
                    }
                    // 如果hash相同，并且key相同，表示存在形同的元素，直接跳出循环
                    if(e.hash == hash &&
                            ((k = e.key) == key || (key != null && key.equals(e.key))))
                        break;
                    // p指向链表中的下一个
                    p = e;
                }
            }
            // 映射关系存在
            if(e != null){
                V oldValue = e.value;
                // 如果onlyIfAbsent为false(表示可以修改已经存在的值)
                // 或者映射的value为null(表示key映射了一个null值)
                // 把当前的值添加到映射当中
                if(!onlyIfAbsent || oldValue == null)
                    e.value = value;
                afterNodeAccess(e);
                return oldValue;
            }

        }
        // 修改次数增加一次
        ++modCount;
        // 元素数量增加一个，如果超出了有效的存储空间
        // 重构HashMap
        if(++size > threshold)
            resize();
        afterNodeInsertion(evict);
        return null;
    }

    final Node<K,V>[] resize(){return null;}

    /**
     * 替换给定散列的索引处的bin中的所有链接节点，除非表太小，在这种情况下调整大小
     * */
    final void treeifyBin(Node<K,V>[] tab, int hash) {}

    /** 创建一个节点 */
    Node<K,V> newNode(int hash, K key, V value, Node<K,V> next) {
        return new Node<>(hash, key, value, next);
    }

    // 回调允许LinkedHashMap后期操作
    void afterNodeAccess(Node<K,V> p) { }
    void afterNodeInsertion(boolean evict) { }
    void afterNodeRemoval(Node<K,V> p) { }

    static final class TreeNode<K,V> extends LinkedHashMap.Entry<K,V>{
        TreeNode<K,V> parent;
        TreeNode<K,V> left;
        TreeNode<K,V> right;
        TreeNode<K,V> prev;
        boolean red;
        TreeNode(int hash, K key, V value, Node<K, V> next) {super(hash, key, value, next);}

        /**
         * 返回树中本结点的根节点
         * */
        final TreeNode<K,V> root(){
            for(TreeNode<K,V> r=this,p;;){
                if((p=r.parent) == null)
                    return r;
                r = p;
            }
        }

        final TreeNode<K,V> putTreeVal(HashMap<K,V> map, Node<K,V>[] tab,
                                       int h, K k, V v) {
            return null;
        }
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return null;
    }

    public static void main(String[] args) {
        System.out.println(tableSizeFor(6));
    }
}
