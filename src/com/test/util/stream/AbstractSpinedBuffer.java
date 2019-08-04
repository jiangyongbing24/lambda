package com.test.util.stream;

/**
 *用于将元素收集到缓冲区中的数据结构的基类
 *迭代它们。 维护一个数量越来越大的数组，所以比
 *没有增长的数据结构多了相应的复制成本。
 */
abstract class AbstractSpinedBuffer {
    /** 第一个块的最小的2的幂数量 */
    public static final int MIN_CHUNK_POWER = 4;

    /**  第一个块的最小大小 */
    public static final int MIN_CHUNK_SIZE = 1 << MIN_CHUNK_POWER;

    /** 块的最大的2的幂数量 */
    public static final int MAX_CHUNK_POWER = 30;

    /** 数组块最小的大小 */
    public static final int MIN_SPINE_SIZE = 8;

    /** 第一个块的2的幂数量 */
    protected final int initialChunkPower;

    /** 要写入的下一个元素的索引; 可以指向当前块或仅在当前块之外。 */
    protected int elementIndex;

    /** 如果spine数组为非null，则为当前块在spine数组中的索引*/
    protected int spineIndex;

    /** 所有先前块的元素总计数 */
    protected long[] priorElementCount;

    /** 构建初始容量为16 */
    protected AbstractSpinedBuffer() {
        this.initialChunkPower = MIN_CHUNK_POWER;
    }

    /** 使用指定的初始容量构造，值得注意的是，这个值是直接指定元素的数量，而不是2的幂的大小 */
    protected AbstractSpinedBuffer(int initialCapacity){
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal Capacity: "+ initialCapacity);

        /**
         * 其中initialCapacity - 1是为了开销最小化的向上兼容 initialCapacity
         *
         * 因为 AbstractSpinedBuffer 在容量不够的时候，每次都会以2的倍数增加容量存储
         * 所以一定会有 AbstractSpinedBuffer 的容量大于等于传递进来的 initialCapacity数量
         * 而为了得到开销最小化的容量，就需要找到一个n有 2^(n - 1) < initialCapacity <= 2^n
         *
         * Integer.SIZE - Integer.numberOfLeadingZeros(initialCapacity)
         * 上述式子会得到 initialCapacity 的二进制位数n，而2^n一定是大于 initialCapacity的，
         * 因为二进制表达的最低位为0，这就决定二进制的最高位为2^(n-1)，所以一定有2^n大于位数为n的二进制
         * 但是为了避免initialCapacity二进制最高位为1，其他均为0的情况而造成的浪费的容量开销，所以采用 initialCapacity - 1i
         *
         * 举例：
         * initialCapacity = 16
         * Integer.SIZE - Integer.numberOfLeadingZeros(initialCapacity) = 5
         * Integer.SIZE - Integer.numberOfLeadingZeros(initialCapacity - 1) = 4
         * 如果initialCapacity不减掉1，那么就会使用2^5=32的容量存储只有16个元素的情况
         * 如果initialCapacity减掉1之后再向上兼容，就不会存在这种情况
         * */
        this.initialChunkPower = Math.max(MIN_CHUNK_POWER
                ,Integer.SIZE - Integer.numberOfLeadingZeros(initialCapacity - 1));
    }

    /** 是否为空 */
    public boolean isEmpty(){return (spineIndex == 0) && (elementIndex == 0);}

    /** 缓冲区元素的数量 */
    public long count(){
        return (spineIndex == 0)
                ? elementIndex
                : priorElementCount[spineIndex] + elementIndex;
    }

    /** 第n次的块的容量 */
    protected int chunkSize(int n){
        int power = (n == 0 || n == 1)
                ? initialChunkPower
                : Math.min(initialChunkPower + n - 1,AbstractSpinedBuffer.MAX_CHUNK_POWER);
        return 1 << power;
    }

    /** 清除所有的缓存数据 */
    public abstract void clear();
}
