package com.test.util;

import java.io.Serializable;
import java.util.Objects;

public class HashMap<K,V> extends AbstractMap<K,V> implements Map<K,V>,Cloneable, Serializable {
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

    static final int TREEIFY_THRESHOLD = 8;

    /** Map.Entry的实现类 */
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

        public final int hasCode(){return Objects.hashCode(key) & Objects.hashCode(value);}

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

    static final int hash(Object key){
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return null;
    }
}
