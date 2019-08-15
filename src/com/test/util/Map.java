package com.test.util;

import com.test.util.function.BiConsumer;
import com.test.util.function.BiFunction;
import com.test.util.function.Function;

import java.io.Serializable;
import java.util.ConcurrentModificationException;
import java.util.Objects;

/** 一个键值对映射的抽象接口 */
public interface Map<K,V> {
    /** 返回键值对映射的数量 */
    int size();

    /** 是否为空 */
    boolean isEmpty();

    /** 是否包含某个key */
    boolean containsKey(Object key);

    /** 是否包含某个值 */
    boolean containsValue(Object value);

    /** 根据key获取值 */
    V get(Object key);

    /** 往map里面添加键值对映射 */
    V put(K key, V value);

    /** 根据key移除键值对映射 */
    V remove(Object key);

    /** 把m中所有的元素添加到当前映射当中 */
    void putAll(Map<? extends K, ? extends V> m);

    /** 清除 */
    void clear();

    /** 返回此Map中的key组成 */
    Set<K> keySet();

    /** 返回value组成的集合 */
    Collection<V> values();

    /**
     * 返回一个Set，这个Set存储的是当前map的条目
     * */
    Set<Map.Entry<K, V>> entrySet();

    /**
     * Map存储的是多个键值对映射，
     * 而Map.Entry<K, V>就是一个存储单个映射条目
     * */
    interface Entry<K,V> {
        // 返回条目的Key
        K getKey();

        // 返回条目的值
        V getValue();

        // 修改条目的值，并且返回值修改之前的值
        V setValue(V value);

        boolean equals(Object o);

        int hashCode();

        /** 返回一个根据条目的key来比较条目的比较器 */
        public static <K extends Comparable<? super K>, V> Comparator<Map.Entry<K,V>> comparingByKey() {
            return (Comparator<Map.Entry<K, V>> & Serializable)//表示强制转换为同时满足两个接口特性的类
                    (c1, c2) -> c1.getKey().compareTo(c2.getKey());
        }

        /** 返回一个根据条目的value来比较条目的比较器 */
        public static <K, V extends Comparable<? super V>> Comparator<Map.Entry<K,V>> comparingByValue() {
            return (Comparator<Map.Entry<K, V>> & Serializable)//表示强制转换为同时满足两个接口特性的类
                    (c1, c2) -> c1.getValue().compareTo(c2.getValue());
        }

        /** 返回一个根据条目的key来比较条目的比较器，此比较器是使用cmp实现的 */
        public static <K, V> Comparator<Map.Entry<K, V>> comparingByKey(Comparator<? super K> cmp) {
            Objects.requireNonNull(cmp);
            return (Comparator<Map.Entry<K, V>> & Serializable)//表示强制转换为同时满足两个接口特性的类
                    (c1, c2) -> cmp.compare(c1.getKey(), c2.getKey());
        }

        /** 返回一个根据条目的value来比较条目的比较器，此比较器是使用cmp实现的 */
        public static <K, V> Comparator<Map.Entry<K, V>> comparingByValue(Comparator<? super V> cmp) {
            Objects.requireNonNull(cmp);
            return (Comparator<Map.Entry<K, V>> & Serializable)//表示强制转换为同时满足两个接口特性的类
                    (c1, c2) -> cmp.compare(c1.getValue(), c2.getValue());
        }
    }

    boolean equals(Object o);

    int hashCode();

    /** 如何存在键等于key的元素，返回键值，否则返回默认值defaultValue */
    default V getOrDefault(Object key, V defaultValue) {
        V v;
        return (((v = get(key)) != null) || containsKey(key))//存在key的条目
                ? v
                : defaultValue;
    }

    /** 循环Map中的条目 */
    default void forEach(BiConsumer<? super K, ? super V> action) {
        Objects.requireNonNull(action);
        for (Map.Entry<K, V> entry : entrySet()) {
            K k;
            V v;
            try {
                k = entry.getKey();
                v = entry.getValue();
            } catch(IllegalStateException ise) {
                // 这通常意味着输入不再在Map当中
                throw new ConcurrentModificationException(ise);
            }
            action.accept(k, v);
        }
    }

    /** 使用function替换掉所有的条目的value */
    default void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        Objects.requireNonNull(function);
        for (Map.Entry<K, V> entry : entrySet()) {
            K k;
            V v;
            try {
                k = entry.getKey();
                v = entry.getValue();
            } catch(IllegalStateException ise) {
                // this usually means the entry is no longer in the map.
                throw new ConcurrentModificationException(ise);
            }

            // 根据function得到替换得值
            v = function.apply(k, v);

            try {
                entry.setValue(v);//修改当前条目得值
            } catch(IllegalStateException ise) {
                // this usually means the entry is no longer in the map.
                throw new ConcurrentModificationException(ise);
            }
        }
    }

    /** 如果键key尚未关联value，添加映射，否则不添加，并且返回value */
    default V putIfAbsent(K key, V value) {
        V v = get(key);
        if (v == null) {
            v = put(key, value);
        }

        return v;
    }

    /**
     * 移除键等于key，并且值等于value的条目
     * 如果条目不存在，返回false
     * */
    default boolean remove(Object key, Object value) {
        Object curValue = get(key);
        if (!Objects.equals(curValue, value) ||
                (curValue == null && !containsKey(key))) {
            return false;
        }
        remove(key);
        return true;
    }

    /**
     * 使用newValue替换掉键等于key，并且值等于oldValue的条目的value
     * 只有存在的时候操作成功
     * */
    default boolean replace(K key, V oldValue, V newValue) {
        Object curValue = get(key);//获取值
        //如果值不对等，获取这不含有值，返回false
        if (!Objects.equals(curValue, oldValue) ||
                (curValue == null && !containsKey(key))) {
            return false;
        }
        put(key, newValue);
        return true;
    }

    /**
     * 使用value替换掉键等于key的条目的值
     * 只有存在的时候操作成功
     * */
    default V replace(K key, V value) {
        V curValue;
        if (((curValue = get(key)) != null) || containsKey(key)) {
            curValue = put(key, value);
        }
        return curValue;
    }

    /**
     * 如果key的值不缺席，不做任何处理，返回值
     * 如果key的值缺席，使用mappingFunction生成一个值，
     * 如果重新生成的这个值不为null，把条目插入Map
     * */
    default V computeIfAbsent(K key,
                              Function<? super K, ? extends V> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        V v;
        if ((v = get(key)) == null) {//如果条目的值为空
            V newValue;
            //使用mappingFunction把key转换为对应的值
            if ((newValue = mappingFunction.apply(key)) != null) {
                put(key, newValue);
                return newValue;
            }
        }

        return v;
    }

    /**
     * 如果key的值缺席，不做任何处理，返回null
     * 如果key的值不缺席，使用remappingFunction生成一个值，
     * 如果重新生成的这个值不为null，把条目插入Map，
     * 否则删除Map中键为key的条目
     * */
    default V computeIfPresent(K key,
                               BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        V oldValue;
        //如果条目的值存在
        if ((oldValue = get(key)) != null) {
            V newValue = remappingFunction.apply(key, oldValue);//获取一个新值
            if (newValue != null) {//新值不为null
                put(key, newValue);
                return newValue;
            } else {
                remove(key);
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 如果通过remappingFunction生成的值不为null，直接插入
     * 否则判断key是否是Map中的有效的键，如果是，移除掉
     * */
    default V compute(K key,
                      BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        V oldValue = get(key);//获取旧值

        V newValue = remappingFunction.apply(key, oldValue);//使用key、oldValue作为输入，生成一个新值
        if (newValue == null) {//如果生成的值为null
            //原来的旧值不为null
            if (oldValue != null || containsKey(key)) {
                // 移除掉条目
                remove(key);
                return null;
            } else {
                // nothing to do. Leave things as they were.
                return null;
            }
        } else {
            // add or replace old mapping
            put(key, newValue);
            return newValue;
        }
    }

    /**
     * 如果指定的键尚未与值关联或者是与null关联，
     * 将其与给定的非null值value相关联
     * 否则，将相关值替换为给定的value和旧值重新映射得到的结果，
     * 如果这个重新生成的值为null，移除掉，否则添加
     * */
    default V merge(K key, V value,
                    BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        Objects.requireNonNull(value);
        V oldValue = get(key);
        V newValue = (oldValue == null) ? value :
                remappingFunction.apply(oldValue, value);
        if(newValue == null) {
            remove(key);
        } else {
            put(key, newValue);
        }
        return newValue;
    }
}
