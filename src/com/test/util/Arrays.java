package com.test.util;

import java.lang.reflect.Array;

public class Arrays {
    /** 复制数组original到一个指定长度为newLength的数组 */
    public static <T,U> T[] copyOf(U[] original,int newLength){
        return (T[])copyOf(original,newLength,original.getClass());
    }

    /** 复制数组original到一个指定长度为newLength的newType类型的数组 */
    public static <T,U> T[] copyOf(U[] original,int newLength,Class<? extends T[]> newType){
        T[] copy = ((Object)newType == (Object)Object[].class)
                ? (T[])new Object[newLength]
                : (T[])Array.newInstance(newType.getComponentType(),newLength);
        System.arraycopy(original,0,copy,0,Math.min(original.length,newLength));
        return copy;
    }

    /** 复制long数组original到一个指定长度为newLength的int类型的数组 */
    public static int[] copyOf(int[] original,int newLength){
        int[] copy = new int[newLength];
        System.arraycopy(original,0,copy,0,Math.min(original.length,newLength));
        return copy;
    }

    /** 复制long数组original到一个指定长度为newLength的long类型的数组 */
    public static long[] copyOf(long[] original,int newLength){
        long[] copy = new long[newLength];
        System.arraycopy(original,0,copy,0,Math.min(original.length,newLength));
        return copy;
    }

    /** 复制double数组original到一个指定长度为newLength的long类型的数组 */
    public static double[] copyOf(double[] original,int newLength){
        double[] copy = new double[newLength];
        System.arraycopy(original,0,copy,0,Math.min(original.length,newLength));
        return copy;
    }

    /** 返回一个数组分区迭代器 */
    public static <T> Spliterator<T> spliterator(T[] array, int startInclusive, int endExclusive){
        return Spliterators.spliterator(array,startInclusive,endExclusive,Spliterator.ORDERED | Spliterator.IMMUTABLE);
    }

    /** 返回一个int数组分区迭代器 */
    public static Spliterator.OfInt spliterator(int[] array, int startInclusive, int endExclusive) {
        return Spliterators.spliterator(array, startInclusive, endExclusive,
                Spliterator.ORDERED | Spliterator.IMMUTABLE);
    }

    /** 返回一个long数组分区迭代器 */
    public static Spliterator.OfLong spliterator(long[] array, int startInclusive, int endExclusive) {
        return Spliterators.spliterator(array, startInclusive, endExclusive,
                Spliterator.ORDERED | Spliterator.IMMUTABLE);
    }

    /** 返回一个double数组分区迭代器 */
    public static Spliterator.OfDouble spliterator(double[] array, int startInclusive, int endExclusive) {
        return Spliterators.spliterator(array, startInclusive, endExclusive,
                Spliterator.ORDERED | Spliterator.IMMUTABLE);
    }

    /** 输出Object[]的信息 */
    public static String toString(Object[] a) {
        if (a == null)
            return "null";

        int iMax = a.length - 1;
        if (iMax == -1)
            return "[]";

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(String.valueOf(a[i]));
            if (i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }

    /** 输出int[]的信息 */
    public static String toString(int[] a) {
        if (a == null)
            return "null";
        int iMax = a.length - 1;
        if (iMax == -1)
            return "[]";

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(a[i]);
            if (i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }

    /** 输出long[]的信息 */
    public static String toString(long[] a) {
        if (a == null)
            return "null";
        int iMax = a.length - 1;
        if (iMax == -1)
            return "[]";

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(a[i]);
            if (i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }

    /** 输出double[]的信息 */
    public static String toString(double[] a) {
        if (a == null)
            return "null";
        int iMax = a.length - 1;
        if (iMax == -1)
            return "[]";

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(a[i]);
            if (i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }

    /** 使用指定的val填充long数组a */
    public static void fill(long[] a, long val) {
        for (int i = 0, len = a.length; i < len; i++)
            a[i] = val;
    }

    /** 比较两个long数组是否相等 */
    public static boolean equals(long[] a, long[] a2) {
        if (a==a2)
            return true;
        if (a==null || a2==null)
            return false;

        int length = a.length;
        if (a2.length != length)
            return false;

        for (int i=0; i<length; i++)
            if (a[i] != a2[i])
                return false;

        return true;
    }
}
