package com.test.util.stream;

import com.test.util.Spliterator;
import com.test.util.function.Consumer;

import java.util.Objects;

final class Streams {
    /**
     * 一个 Stream.Builder<T> 的实现类
     * 继承AbstractStreamBuilderImpl的目的是记录流状态
     * */
    static final class StreamBuilderImpl<T>
            extends AbstractStreamBuilderImpl<T,Spliterator<T>>
            implements Stream.Builder<T>{
        //Stream中的第一个元素
        T first;

        //缓存Stream中第一个以及之后的元素
        //如果count == 2，则不为null
        SpinedBuffer<T> buffer;

        /** 用于构建0个或更多元素的流的构造函数 */
        StreamBuilderImpl(){}

        /** 单例流的构造函数 */
        StreamBuilderImpl(T t){
            this.first = t;
            count = -2;
        }

        /** 向正在构建的流添加元素 */
        @Override
        public void accept(T t) {
            //存储的元素大于1的时候，count就会锁定为2，在build()的时候会-count-1
            if(count == 0){//如果还没有元素，初始化第一个元素
                first = t;
                count++;
            }
            else if(count > 0){//如果已经含有元素
                if(buffer == null){//如果缓存池还没初始化
                    buffer = new SpinedBuffer<>();
                    buffer.accept(first);//添加第一个元素
                    count++;
                }
                buffer.accept(t);//添加新元素进入缓存
            }
            else {
                throw new IllegalStateException();
            }
        }

        /** 向正在构建的流添加元素，返回的是一个新的Builder，可以再次添加元素 */
        public Stream.Builder<T> add(T t){
            accept(t);
            return this;
        }

        /**
         * 构建Stream，并且把Stream的状态修改为已经构建
         * */
        @Override
        public Stream<T> build() {
            int c = count;
            if(c >= 0){//如果在构造中
                // 状态修改为构建完成，并且多减了一个1
                // 正好对应AbstractStreamBuilderImpl的count的定义
                count = -count - 1;
                return (c < 2)//如果结束构建的时候只有0个或者1个元素
                        ? StreamSupport.stream(this, false)
                        : StreamSupport.stream(buffer.getSpliterator(), false);

            }
            throw new IllegalStateException();
        }

        /** 只有当Stream的状态变为已构建，并且流中只有一个元素的时候才会触发action */
        @Override
        public boolean tryAdvance(Consumer<? super T> action) {
            Objects.requireNonNull(action);
            if (count == -2) {//如果是构建完成的，并且有一个元素
                action.accept(first);
                count = -1;//表示没有元素了
                return true;
            }
            else {
                return false;
            }
        }

        /** 只有当Stream的状态变为已构建，并且流中只有一个元素的时候才会触发action */
        @Override
        public void forEachRemaining(Consumer<? super T> action) {
            Objects.requireNonNull(action);

            if (count == -2) {
                action.accept(first);
                count = -1;
            }
        }
    }

    /**
     * 用于0个或者1个元素的Spliterator实现
     * count == -1表示没有元素
     * count == -2表示持有一个元素
     * */
    static abstract class AbstractStreamBuilderImpl<T,S extends Spliterator<T>> implements Spliterator<T> {
        /**
         * 当 >= 0 的时候表示正在构建，< 0 的时候表示构建完成
         * -1 表示没有元素
         * -2 表示有一个元素，由第一个元素持有
         * -3 表示两个及以上元素，由缓冲区持有
         * */
        int count = 0;

        @Override
        public S trySplit(){return null;}

        @Override
        public long estimateSize() {
            return -count - 1;
        }

        @Override
        public int characteristics() {
            return Spliterator.SIZED | Spliterator.SUBSIZED |
                    Spliterator.ORDERED | Spliterator.IMMUTABLE;
        }
    }

    /**
     * 给定两个Runnable，返回一个Runnable，它会按顺序执行这两个Runnable，即使第一个抛出异常，
     * 如果两个都抛出异常，添加第二个抛出的任何异常作为第一个的抑制异常
     */
    static Runnable composeWithExceptions(Runnable a, Runnable b) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    a.run();
                }
                catch (Throwable e1) {
                    try {
                        b.run();
                    }
                    catch (Throwable e2) {
                        try {
                            e1.addSuppressed(e2);
                        } catch (Throwable ignore) {}
                    }
                    throw e1;
                }
                b.run();
            }
        };
    }
}
