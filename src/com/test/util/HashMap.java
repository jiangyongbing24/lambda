package com.test.util;

import java.io.Serializable;

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

    @Override
    public Set<Entry<K, V>> entrySet() {
        return null;
    }
}
