package com.test.util.stream;

import com.test.util.Map;

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
    DISTINCT;

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

    private static class MaskBuilder{
        final Map<Type, Integer> map;

        MaskBuilder(Map<Type, Integer> map) {
            this.map = map;
        }
    }
}
