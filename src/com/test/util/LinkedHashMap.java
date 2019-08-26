package com.test.util;

public class LinkedHashMap<K,V> extends HashMap<K,V> {
    static class Entry<K,V> extends HashMap.Node<K,V>{
        Entry<K,V> before,after;
        Entry(int hash, K key, V value, Node<K, V> next) {super(hash, key, value, next);}
    }
}
