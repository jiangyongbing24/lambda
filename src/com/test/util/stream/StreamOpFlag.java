package com.test.util.stream;

import com.test.util.EnumMap;
import com.test.util.Map;
import com.test.util.Spliterator;
import test.Test;

/**
 * 标志对应于流和操作的特征
 * 标志用于流框架的控制，专业化或者优化计算
 *
 * 流标志可以用于描述流相关的实体：流源，中间操作，终端操作等
 * 不是所有的标志在所有方面上都具有意义，下表描述了不同的标志在各个方面是否具有意义
 *                            Type Characteristics
 *                          DISTINCT    SORTED    ORDERED    SIZED    SHORT_CIRCUIT
 * Stream source               Y           Y         Y         Y            N
 * Intermediate operation     PCI         PCI       PCI       PCI          PI
 * Terminal operation          N           N        PC         N           PI
 *
 * 其中上数的表格当中各个标志代表的意思是
 * Y - Allowed(允许) N - Invalid(无效) P - Preserves(保留) C - Clears(清除) I - Injects(注入)
 * */
enum StreamOpFlag {
    /**
     * 下表示了不同的Type在StreamOpFlag的设置情况
     *                            0        1        2       3        12
     *                        DISTINCT  SORTED  ORDERED  SIZED  SHORT_CIRCUIT
     *          SPLITERATOR      01       01       01      01        00
     *               STREAM      01       01       01      01        00
     *                   OP      11       11       11      10        01
     *          TERMINAL_OP      00       00       10      00        01
     * UPSTREAM_TERMINAL_OP      00       00       10      00        00
     * */

    DISTINCT(0,set(Type.SPLITERATOR).set(Type.STREAM).setAndClear(Type.OP)),

    SORTED(1,set(Type.SPLITERATOR).set(Type.STREAM).setAndClear(Type.OP)),

    ORDERED(2,
            set(Type.SPLITERATOR).set(Type.STREAM).setAndClear(Type.OP).clear(Type.TERMINAL_OP)
                    .clear(Type.UPSTREAM_TERMINAL_OP)),

    SIZED(3,set(Type.SPLITERATOR).set(Type.STREAM).clear(Type.OP)),

    SHORT_CIRCUIT(12,set(Type.OP).set(Type.TERMINAL_OP));

    /**
     * 标志类型
     */
    enum Type {
        /**
         * 该标志与分裂期特征相关联
         */
        SPLITERATOR,

        /**
         * 该标志与流特征相关联
         */
        STREAM,

        /**
         * 该标志与中间操作标志相关联
         */
        OP,

        /**
         * 该标志与终端操作标志相关联
         */
        TERMINAL_OP,

        /**
         * 该标志与终端操作标志相关联，在最后一个有状态操作边界上游传播
         */
        UPSTREAM_TERMINAL_OP
    }

    /** 用户设置/注入的位模式 */
    private static final int SET_BITS = 0b01;

    /** 用于清除标志的位模式 */
    private static final int CLEAR_BITS = 0b10;

    /** 用于保留标志的位模式 */
    private static final int PRESERVE_BITS = 0b11;

    private static MaskBuilder set(Type t){
        return new MaskBuilder(new EnumMap<>(Type.class)).set(t);
    }

    /** 一个掩码的构建类 */
    private static class MaskBuilder{
        final Map<Type, Integer> map;

        MaskBuilder(Map<Type, Integer> map) {this.map = map;}

        MaskBuilder mask(Type t,Integer i){
            map.put(t,i);
            return this;
        }

        MaskBuilder set(Type t){return mask(t,SET_BITS);}

        MaskBuilder clear(Type t){return mask(t,CLEAR_BITS);}

        MaskBuilder setAndClear(Type t){return mask(t,PRESERVE_BITS);}

        Map<Type,Integer> build(){
            for(Type t : Type.values()){
                map.putIfAbsent(t,0b00);
            }
            return map;
        }
    }

    /**
     * 一个标志的掩码表，用于确定是否含有标志
     * 对应于某种标志类型并用于创建掩码常量
     * */
    private final Map<Type,Integer> maskTable;

    /** 位掩码中位的位置 */
    private final int bitPosition;

    private final int set;

    private final int clear;

    private final int preserve;

    /** 构造函数，初始化maskTable即一些常量 */
    private StreamOpFlag(int position,MaskBuilder maskBuilder){
        this.maskTable = maskBuilder.build();
        position *= 2;
        this.bitPosition = position;
        this.set = SET_BITS << position;
        this.clear = CLEAR_BITS << position;
        this.preserve = PRESERVE_BITS << position;
    }

    int set(){return set;}

    int clear(){return clear;}

    /** 确定此标志是否是基于流的标志 */
    boolean isStreamFlag(){return maskTable.get(Type.STREAM) > 0;}

    /** 检查是否在流标志上设置了此标志，并在操作标志上注入 */
    boolean isKnown(int flags){return (flags & preserve) == set;}

    /** 检查操作标志或组合流和操作标志是否清除此标志 */
    boolean isCleared(int flags){return (flags & preserve) == clear;}

    /** 检查组合流和操作标志是否含有保留此标志 */
    boolean isPreserved(int flags) {return (flags & preserve) == preserve;}

    /** 确定是否可以为标志类型设置此标志 */
    boolean canSet(Type t){return (maskTable.get(t) & SET_BITS) > 0;}

    // Type.SPLITERATOR的掩码
    static final int SPLITERATOR_CHARACTERISTICS_MASK = creatMask(Type.SPLITERATOR);

    // Type.STREAM的掩码
    static final int STREAM_MASK = creatMask(Type.STREAM);

    // Type.OP的掩码
    static final int OP_MASK = creatMask(Type.OP);

    // Type.TERMINAL_OP的掩码
    static final int TERMINAL_OP_MASK = creatMask(Type.TERMINAL_OP);

    // Type.UPSTREAM_TERMINAL_OP的掩码
    static final int UPSTREAM_TERMINAL_OP_MASK = creatMask(Type.UPSTREAM_TERMINAL_OP);

    /**
     * 创建一个Type的掩码，表示了此Type在StreamOpFlag的分布情况
     * 比如对于Type.SPLITERATOR，它在StreamOpFlag上的设置情况如下：
     *                   0        1        2      3         12
     *               DISTINCT  SORTED  ORDERED  SIZED  SHORT_CIRCUIT
     * SPLITERATOR      01       01       01      01        00
     * 那么Type.SPLITERATOR的掩码计算如下：
     * 取StreamOpFlag.DISTINCT.maskTable中键为Type.SPLITERATOR的条目的值，然后左移0*2位 = 0b1
     * 取StreamOpFlag.SORTED.maskTable中键为Type.SPLITERATOR的条目的值，然后左移1*2位 = 0b100
     * 取StreamOpFlag.ORDERED.maskTable中键为Type.SPLITERATOR的条目的值，然后左移2*2位 = 0b10000
     * 取StreamOpFlag.SIZED.maskTable中键为Type.SPLITERATOR的条目的值，然后左移3*2位 = 0b1000000
     * 取StreamOpFlag.SHORT_CIRCUIT.maskTable中键为Type.SPLITERATOR的条目的值，然后左移12*2位 = 0b0
     * 把上述所有的值从上到下进行或运算，得到掩码 = 0b1010101，其他的Type计算类似
     * */
    private static int creatMask(Type t){
        int mask = 0;
        for (StreamOpFlag flag : StreamOpFlag.values()) {
            mask |= flag.maskTable.get(t) << flag.bitPosition;
        }
        return mask;
    }

    //完成标志掩码
    //0b00000011000000000000000011111111
    private static int FLAG_MASK = createFlagMask();

    //创建StreamOpFlag的完成标志掩码
    private static int createFlagMask(){
        int mask = 0;
        for(StreamOpFlag flag : StreamOpFlag.values()){
            mask |= flag.preserve;
        }
        return mask;
    }

    //Stream标志的掩码
    private static final int FLAG_MASK_IS = STREAM_MASK;

    //清除流标志的标志掩码
    private static final int FLAG_MASK_NOT = STREAM_MASK << 1;

    //一个用来结合管道中第一个流标志的初始值 = 0b11111111
    static final int INITIAL_OPS_VALUE = FLAG_MASK_IS | FLAG_MASK_NOT;

    //要设置或注入DISTINCT的位值
    static final int IS_DISTINCT = DISTINCT.set;

    //用于清除DISTINCT的位值
    static final int NOT_DISTINCT = DISTINCT.clear;

    //要设置或注入SORTED的位值
    static final int IS_SORTED = SORTED.set;

    //用于清除SORTED的位值
    static final int NOT_SORTED = SORTED.clear;

    //要设置或注入ORDERED的位值
    static final int IS_SORDERED = ORDERED.set;

    //用于清除ORDERED的位值
    static final int NOT_ORDERED = ORDERED.clear;

    //要设置或注入SIZED的位值
    static final int IS_SIZED = SIZED.set;

    //用于清除SIZED的位值
    static final int NOT_SIZED = SIZED.clear;

    //要设置或注入SHORT_CIRCUIT的位值
    static final int IS_SHORT_CIRCUIT = SHORT_CIRCUIT.set;

    //用于清除SHORT_CIRCUIT的位值
    static final int NOT_SHORT_CIRCUIT = SHORT_CIRCUIT.clear;

    /**
     * 根据传入的flags，生成一个掩码，
     * 这个掩码用来过滤掉这个数字所涉及到的位区间的位值
     * 比如对于17 = 0b10001，以两位为一个位区间，那么掩码最多会过滤掉51 = Ob110011
     * 生成的掩码为-52 = Ob11111111111111111111111111001100
     * */
    private static int getMask(int flags){
        return (flags == 0)
                ? FLAG_MASK // 如果flags为0，直接返回完成标志掩码
                // FLAG_MASK_IS & flags会只保留flags在每个位区间的低的那个位值，只关注低位，
                // flags | ((FLAG_MASK_IS & flags) << 1)表示，
                // 如果flags在此位区间有一个位的低位为1，那么把此区间两个位上的位都设为1，
                // FLAG_MASK_NOT & flags会只保留flags在每个位区间的高的那个位值，只关注高位，
                // (FLAG_MASK_NOT & flags) >> 1把此高位的1放到低位
                // 最终取补码
                : ~(flags | ((FLAG_MASK_IS & flags) << 1) | ((FLAG_MASK_NOT & flags) >> 1));
    }

    /**
     * 将流或操作标志与先前组合的流和操作标志组合
     * 以产生更新的组合流和操作标志
     * */
    static int combineOpFlags(int newStreamOrOpFlags,int prevCombOpFlags){
        // 不直接使用prevCombOpFlags | newStreamOrOpFlags是因为只想结合位区间内的数
        // prevCombOpFlags & StreamOpFlag.getMask(newStreamOrOpFlags)
        // 清空了prevCombOpFlags在newStreamOrOpFlags所属的位区间
        return (prevCombOpFlags & StreamOpFlag.getMask(newStreamOrOpFlags)) | newStreamOrOpFlags;
    }

    /** 将spliterator特征位设置转换为流标志 */
    static int fromCharacteristics(Spliterator<?> spliterator){
        //获取spliterator的特征位
        int characteristics = spliterator.characteristics();
        //如果spliterator含有Spliterator.SORTED特征，并且比较器不为空
        if((characteristics & Spliterator.SORTED) != 0 && spliterator.getComparator() != null){
            //根据Spliterator的掩码，保留对应的特征位，并且去掉Spliterator.SORTED特征
            return characteristics & SPLITERATOR_CHARACTERISTICS_MASK & ~Spliterator.SORTED;
        }
        else{
            return characteristics & SPLITERATOR_CHARACTERISTICS_MASK;
        }
    }

    public static void main(String[] args) {
        int mask = StreamOpFlag.creatMask(Type.STREAM);
//        System.out.println(Test.IntegerTo32BinaryString(mask));
//        System.out.println(Test.IntegerTo32BinaryString(18));
//        System.out.println(Test.IntegerTo32BinaryString(StreamOpFlag.getMask(18)));
        int a = 1;
        int b = 514;
        System.out.println(Test.IntegerTo32BinaryString(a));
        System.out.println(Test.IntegerTo32BinaryString(b));
        System.out.println(Test.IntegerTo32BinaryString(StreamOpFlag.combineOpFlags(a,b)));
    }
}
