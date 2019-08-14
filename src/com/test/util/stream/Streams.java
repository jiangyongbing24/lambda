package com.test.util.stream;

import com.test.util.Spliterator;
import com.test.util.function.Consumer;

final class Streams {
    /**
     * 一个 Stream.Builder<T> 的实现类
     * */
    static final class StreamBuilderImpl<T>
            extends AbstractStreamBuilderImpl<T,Spliterator<T>>
            implements Stream.Builder<T>{
        //Stream中的第一个元素
        T first;

        //缓存Stream中第一个以及之后的元素
        //如果count == 2，则不为null
        SpinedBuffer<T> buffer;

        StreamBuilderImpl(){}

        StreamBuilderImpl(T t){
            this.first = t;
            count = -2;
        }

        @Override
        public void accept(T t) {
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

        public Stream.Builder<T> add(T t){
            accept(t);
            return this;
        }

        @Override
        public Stream<T> build() {
            int c = count;
            if(c >= 0){
                count = -count - 1;
                return (c < 2)
                        ? StreamSupport.stream(this, false)
                        : StreamSupport.stream(buffer.getSpliterator(), false);

            }
            throw new IllegalStateException();
        }

        @Override
        public boolean tryAdvance(Consumer<? super T> action) {
            return false;
        }
    }

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
}
