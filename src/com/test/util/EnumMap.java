package com.test.util;

import sun.misc.SharedSecrets;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 使用枚举存储键值的Map类
 * */
public class EnumMap<K extends Enum<K>,V> extends AbstractMap<K,V>
        implements Serializable,Cloneable {
    /** 键的类型 */
    private final Class<K> keyType;

    /** 存储所有的键 */
    private transient K[] keyUniverse;

    /** 存储所有的键值 */
    private transient Object[] vals;

    private transient int size = 0;

    /** 一个代表空值的对象 */
    private static final Object NULL = new Object() {
        public int hashCode() {
            return 0;
        }

        public String toString() {
            return "java.util.EnumMap.NULL";
        }
    };

    /** 如果value为null，返回NULL对象 */
    private Object maskNull(Object value) {
        return (value == null ? NULL : value);
    }

    /** 判断当前的键值是否是NULL对象 */
    @SuppressWarnings("unchecked")
    private V unmaskNull(Object value) {
        return (V)(value == NULL ? null : value);
    }

    private static final Enum<?>[] ZERO_LENGTH_ENUM_ARRAY = new Enum<?>[0];

    /** 根据keyType类型的枚举，初始化EnumMap */
    public EnumMap(Class<K> keyType) {
        this.keyType = keyType;
        keyUniverse = getKeyUniverse(keyType);//获取所有的枚举值
        vals = new Object[keyUniverse.length];
    }

    /** 根据一个EnumMap初始化 */
    public EnumMap(EnumMap<K, ? extends V> m) {
        keyType = m.keyType;
        keyUniverse = m.keyUniverse;
        vals = m.vals.clone();
        size = m.size;
    }

    /** 根据一个Map初始化 */
    public EnumMap(Map<K, ? extends V> m) {
        //如果此Map是EnumMap类型的，步骤跟上面一样
        if (m instanceof EnumMap) {
            EnumMap<K, ? extends V> em = (EnumMap<K, ? extends V>) m;
            keyType = em.keyType;
            keyUniverse = em.keyUniverse;
            vals = em.vals.clone();
            size = em.size;
        } else {
            if (m.isEmpty())
                throw new IllegalArgumentException("Specified map is empty");
            //获取m的所有的key组成一个Set，然后返回这个Set的迭代器
            //然后获取第一个元素的枚举类型
            //使用getDeclaringClass比使用getClass的好处是，避免枚举内部类的错误引用
            keyType = m.keySet().iterator().next().getDeclaringClass();
            keyUniverse = getKeyUniverse(keyType);//获取所有的枚举值
            vals = new Object[keyUniverse.length];
            putAll(m);
        }
    }

    public int size() {
        return size;
    }

    /** 是否包含某个值 */
    public boolean containsValue(Object value) {
        value = maskNull(value);

        for (Object val : vals)
            if (value.equals(val))
                return true;

        return false;
    }

    /** 是否包含某个key */
    public boolean containsKey(Object key) {
        return isValidKey(key) && vals[((Enum<?>)key).ordinal()] != null;
    }

    /** 是否包含某个映射关系 */
    private boolean containsMapping(Object key, Object value) {
        // 如果key是有效的，在vals中取索引等于key.ordinal()的值跟value进行比较
        return isValidKey(key) &&
                maskNull(value).equals(vals[((Enum<?>)key).ordinal()]);
    }

    /** 根据键获取键值 */
    public V get(Object key) {
        return (isValidKey(key) ?
                unmaskNull(vals[((Enum<?>)key).ordinal()]) : null);
    }

    /** 添加键值对 */
    public V put(K key, V value) {
        typeCheck(key);//key不是枚举类型，抛出异常

        int index = key.ordinal();
        Object oldValue = vals[index];
        vals[index] = maskNull(value);
        if (oldValue == null)
            size++;
        return unmaskNull(oldValue);
    }

    /** 根据key移除键值对映射 */
    public V remove(Object key) {
        if (!isValidKey(key))
            return null;
        int index = ((Enum<?>)key).ordinal();
        Object oldValue = vals[index];
        vals[index] = null;
        if (oldValue != null)
            size--;
        return unmaskNull(oldValue);
    }

    private boolean removeMapping(Object key, Object value) {
        if (!isValidKey(key))
            return false;
        int index = ((Enum<?>)key).ordinal();
        if (maskNull(value).equals(vals[index])) {
            vals[index] = null;
            size--;
            return true;
        }
        return false;
    }

    //判断是否为无效的值
    private boolean isValidKey(Object key) {
        if (key == null)
            return false;

        // Cheaper than instanceof Enum followed by getDeclaringClass
        Class<?> keyClass = key.getClass();
        return keyClass == keyType || keyClass.getSuperclass() == keyType;
    }

    /** 把m中所有的元素添加到当前映射当中 */
    public void putAll(Map<? extends K, ? extends V> m) {
        //如果m不是EnumMap类型的，交给AbstractMap处理
        if (m instanceof EnumMap) {
            EnumMap<?, ?> em = (EnumMap<?, ?>)m;
            //如果keyType类型不一样，且m不为空，抛出异常
            if (em.keyType != keyType) {
                if (em.isEmpty())
                    return;
                throw new ClassCastException(em.keyType + " != " + keyType);
            }

            for (int i = 0; i < keyUniverse.length; i++) {
                Object emValue = em.vals[i];
                if (emValue != null) {
                    //只有原来不存在元素的时候size才+1
                    if (vals[i] == null)
                        size++;
                    vals[i] = emValue;
                }
            }
        } else {
            super.putAll(m);
        }
    }

    /** 清空 */
    public void clear() {
        Arrays.fill(vals, null);//使用null填充每个键值
        size = 0;
    }

    private transient Set<Map.Entry<K,V>> entrySet;

    /** 返回此Map中的key组成 */
    public Set<K> keySet() {
        Set<K> ks = keySet;
        if (ks == null) {
            ks = new KeySet();
            keySet = ks;
        }
        return ks;
    }

    /** 用来存储EnumMap的Key的Set实现类 */
    private class KeySet extends AbstractSet<K> {
        public Iterator<K> iterator() {
            return new KeyIterator();
        }
        public int size() {
            return size;
        }
        public boolean contains(Object o) {
            return containsKey(o);
        }
        public boolean remove(Object o) {
            int oldSize = size;
            EnumMap.this.remove(o);
            return size != oldSize;
        }
        public void clear() {
            EnumMap.this.clear();
        }
    }

    /** 返回value组成的集合 */
    public Collection<V> values() {
        Collection<V> vs = values;
        if (vs == null) {
            vs = new Values();
            values = vs;
        }
        return vs;
    }

    /** 用来存储EnumMap的Value的Collectiono实现类 */
    private class Values extends AbstractCollection<V> {
        public Iterator<V> iterator() {
            return new ValueIterator();
        }
        public int size() {
            return size;
        }
        public boolean contains(Object o) {
            return containsValue(o);
        }
        public boolean remove(Object o) {
            o = maskNull(o);

            for (int i = 0; i < vals.length; i++) {
                if (o.equals(vals[i])) {
                    vals[i] = null;
                    size--;
                    return true;
                }
            }
            return false;
        }
        public void clear() {
            EnumMap.this.clear();
        }
    }

    /**
     * 返回一个Set，这个Set存储的是当前map的条目
     * */
    public Set<Map.Entry<K,V>> entrySet() {
        Set<Map.Entry<K,V>> es = entrySet;
        if (es != null)
            return es;
        else
            return entrySet = new EntrySet();
    }

    /** 用来存储EnumMap的Set实现类 */
    private class EntrySet extends AbstractSet<Map.Entry<K,V>> {
        public Iterator<Map.Entry<K,V>> iterator() {
            return new EntryIterator();
        }

        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<?,?> entry = (Map.Entry<?,?>)o;
            return containsMapping(entry.getKey(), entry.getValue());
        }
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<?,?> entry = (Map.Entry<?,?>)o;
            return removeMapping(entry.getKey(), entry.getValue());
        }
        public int size() {
            return size;
        }
        public void clear() {
            EnumMap.this.clear();
        }
        public Object[] toArray() {
            return fillEntryArray(new Object[size]);
        }
        @SuppressWarnings("unchecked")
        public <T> T[] toArray(T[] a) {
            int size = size();
            if (a.length < size)
                a = (T[])java.lang.reflect.Array
                        .newInstance(a.getClass().getComponentType(), size);
            if (a.length > size)
                a[size] = null;
            return (T[]) fillEntryArray(a);
        }
        private Object[] fillEntryArray(Object[] a) {
            int j = 0;
            for (int i = 0; i < vals.length; i++)
                if (vals[i] != null)
                    a[j++] = new AbstractMap.SimpleEntry<>(
                            keyUniverse[i], unmaskNull(vals[i]));
            return a;
        }
    }

    /**
     * EnumMap的抽象迭代器
     * 此抽象接口不能返回具体迭代值
     *  */
    private abstract class EnumMapIterator<T> implements Iterator<T> {
        // 要返回的下一个元素的索引
        int index = 0;

        // 上一个返回元素的索引，如果没有为-1
        int lastReturnedIndex = -1;

        public boolean hasNext() {
            //当前位置的索引没有到最后一位，且当前位置的已经被置为null
            while (index < vals.length && vals[index] == null)
                index++;
            return index != vals.length;
        }

        public void remove() {
            checkLastReturnedIndex();

            if (vals[lastReturnedIndex] != null) {
                vals[lastReturnedIndex] = null;//当前位置索引置位null
                size--;
            }
            lastReturnedIndex = -1;
        }

        // 判断是否已经索引完，如果是的话抛出异常
        private void checkLastReturnedIndex() {
            if (lastReturnedIndex < 0)
                throw new IllegalStateException();
        }
    }

    /** EnumMap的Key迭代器 */
    private class KeyIterator extends EnumMapIterator<K> {
        public K next() {
            if (!hasNext())
                throw new NoSuchElementException();
            lastReturnedIndex = index++;
            return keyUniverse[lastReturnedIndex];//返回key
        }
    }

    /** EnumMap的Value迭代器 */
    private class ValueIterator extends EnumMapIterator<V> {
        public V next() {
            if (!hasNext())
                throw new NoSuchElementException();
            lastReturnedIndex = index++;
            return unmaskNull(vals[lastReturnedIndex]);//返回value
        }
    }

    /** EnumMad的迭代器，以EnumMap.Entry作为组成迭代 */
    private class EntryIterator extends EnumMapIterator<Map.Entry<K,V>> {
        // 最后一个迭代对象
        private Entry lastReturnedEntry;

        public Map.Entry<K,V> next() {
            if (!hasNext())
                throw new NoSuchElementException();
            // 得到当前元素，并且索引+1
            lastReturnedEntry = new Entry(index++);
            return lastReturnedEntry;
        }

        public void remove() {
            // 如果lastReturnedEntry为null，表示迭代完毕，lastReturnedIndex=-1
            // 否则lastReturnedIndex = lastReturnedEntry.index
            lastReturnedIndex =
                    ((null == lastReturnedEntry) ? -1 : lastReturnedEntry.index);
            super.remove();
            lastReturnedEntry.index = lastReturnedIndex;
            lastReturnedEntry = null;
        }

        /** Map.Entry接口实现类 */
        private class Entry implements Map.Entry<K,V> {
            // 记录当前条目在EnumMap中的位置
            private int index;

            private Entry(int index) {
                this.index = index;
            }

            public K getKey() {
                checkIndexForEntryUse();
                return keyUniverse[index];
            }

            public V getValue() {
                checkIndexForEntryUse();
                return unmaskNull(vals[index]);
            }

            public V setValue(V value) {
                checkIndexForEntryUse();
                V oldValue = unmaskNull(vals[index]);
                vals[index] = maskNull(value);
                return oldValue;
            }

            public boolean equals(Object o) {
                if (index < 0)
                    return o == this;

                if (!(o instanceof Map.Entry))
                    return false;

                Map.Entry<?,?> e = (Map.Entry<?,?>)o;
                V ourValue = unmaskNull(vals[index]);
                Object hisValue = e.getValue();
                return (e.getKey() == keyUniverse[index] &&
                        (ourValue == hisValue ||
                                (ourValue != null && ourValue.equals(hisValue))));
            }

            public int hashCode() {
                if (index < 0)
                    return super.hashCode();

                return entryHashCode(index);
            }

            public String toString() {
                if (index < 0)
                    return super.toString();

                return keyUniverse[index] + "="
                        + unmaskNull(vals[index]);
            }

            /** 判断当前条目是否被移除掉了 */
            private void checkIndexForEntryUse() {
                if (index < 0)
                    throw new IllegalStateException("Entry was removed");
            }
        }
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o instanceof EnumMap)
            return equals((EnumMap<?,?>)o);
        if (!(o instanceof Map))
            return false;

        Map<?,?> m = (Map<?,?>)o;
        if (size != m.size())
            return false;

        for (int i = 0; i < keyUniverse.length; i++) {
            if (null != vals[i]) {
                K key = keyUniverse[i];
                V value = unmaskNull(vals[i]);
                if (null == value) {
                    if (!((null == m.get(key)) && m.containsKey(key)))
                        return false;
                } else {
                    if (!value.equals(m.get(key)))
                        return false;
                }
            }
        }

        return true;
    }

    private boolean equals(EnumMap<?,?> em) {
        if (em.keyType != keyType)
            return size == 0 && em.size == 0;

        // Key types match, compare each value
        for (int i = 0; i < keyUniverse.length; i++) {
            Object ourValue =    vals[i];
            Object hisValue = em.vals[i];
            if (hisValue != ourValue &&
                    (hisValue == null || !hisValue.equals(ourValue)))
                return false;
        }
        return true;
    }

    public int hashCode() {
        int h = 0;

        for (int i = 0; i < keyUniverse.length; i++) {
            if (null != vals[i]) {
                h += entryHashCode(i);
            }
        }

        return h;
    }

    private int entryHashCode(int index) {
        return (keyUniverse[index].hashCode() ^ vals[index].hashCode());
    }

    @SuppressWarnings("unchecked")
    public EnumMap<K, V> clone() {
        EnumMap<K, V> result = null;
        try {
            result = (EnumMap<K, V>) super.clone();
        } catch(CloneNotSupportedException e) {
            throw new AssertionError();
        }
        result.vals = result.vals.clone();
        result.entrySet = null;
        return result;
    }

    private void typeCheck(K key) {
        Class<?> keyClass = key.getClass();
        if (keyClass != keyType && keyClass.getSuperclass() != keyType)
            throw new ClassCastException(keyClass + " != " + keyType);
    }

    /**
     * 返回枚举类型为keyType的枚举的所有的枚举值
     * 结果不能克隆，不能缓存，不能共享
     */
    private static <K extends Enum<K>> K[] getKeyUniverse(Class<K> keyType) {
        //返回jvm里面存在的elementType类型的所有值
        return SharedSecrets.getJavaLangAccess()
                .getEnumConstantsShared(keyType);
    }

    private static final long serialVersionUID = 458661240069192865L;

    private void writeObject(java.io.ObjectOutputStream s)
            throws java.io.IOException
    {
        // Write out the key type and any hidden stuff
        s.defaultWriteObject();

        // Write out size (number of Mappings)
        s.writeInt(size);

        // Write out keys and values (alternating)
        int entriesToBeWritten = size;
        for (int i = 0; entriesToBeWritten > 0; i++) {
            if (null != vals[i]) {
                s.writeObject(keyUniverse[i]);
                s.writeObject(unmaskNull(vals[i]));
                entriesToBeWritten--;
            }
        }
    }

    /** 重写反序列化逻辑 */
    @SuppressWarnings("unchecked")
    private void readObject(java.io.ObjectInputStream s)
            throws java.io.IOException, ClassNotFoundException
    {
        // Read in the key type and any hidden stuff
        s.defaultReadObject();

        keyUniverse = getKeyUniverse(keyType);//获取所有的键值
        vals = new Object[keyUniverse.length];//初始化一个vals，数组大小等于Key的数量

        // 获取数组大小
        int size = s.readInt();

        // 循环读取Key和Value，并且通过put方法写入到当前类
        for (int i = 0; i < size; i++) {
            K key = (K) s.readObject();
            V value = (V) s.readObject();
            put(key, value);
        }
    }
}
