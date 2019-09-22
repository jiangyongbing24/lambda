package test;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;;

public class MyCollectors {
    static final Set<Collector.Characteristics> CH_ID
            = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.IDENTITY_FINISH));

    /**
     * 返回一个收集器，这个收集器会把元素收集到一个list里面
     **/
    public static <T> Collector<T,?,List<T>> toList(){
        return new CollectorImpl<>((Supplier<List<T>>) ArrayList::new,
                List::add,(left,right) -> {left.addAll(right);return left;},CH_ID);
    }

    public static <T,K,D,A,M extends Map<K,D>>
    Collector<T,?,M> groupingBy(Function<? super T,? extends K> classifier,
                                Supplier<M> mapFactory,
                                Collector<T,A,D> downStream){
        // 获取下游收集器的供应商
        Supplier<A> downStreamSupplier = downStream.supplier();
        // 获取下游收集器的累加器
        BiConsumer<A,T> downStreamAccumulator = downStream.accumulator();
        // 创建当前的累加器
        BiConsumer<Map<K,A>,T> accumulator = (m,t) -> {
            // 把元素通过分类器classifier映射成一个key
            K key = Objects.requireNonNull(classifier.apply(t),"element cannot be mapped to a null key");
            // 把元素添加进入Map之前，需要先获取值的存储容器，因为把一个集合按照Key分类，每个分类则需要一个容器去保存
            A container = m.computeIfAbsent(key,k -> downStreamSupplier.get());
            // 通过下游收集器的累加器把元素累加到key所在的容器里面
            downStreamAccumulator.accept(container,t);
        };
        // 获取一个Map<K,A>组合函数，如果Map中的Key已经存在值，
        // 则采用下游的组合器，把两个值组合
        BinaryOperator<Map<K,A>> mapCombiner = MyCollectors.mapMerger(downStream.combiner());
        // 创建一个供应商用来提供最终的存储的容器
        Supplier<Map<K,A>> mangledFactory = (Supplier<Map<K,A>>)mapFactory;
        // 如果下游收集器的装订器是身份功能，可以忽略，此时直接返回
        if (downStream.characteristics().contains(Collector.Characteristics.IDENTITY_FINISH)) {
            return new CollectorImpl<>(mangledFactory, accumulator, mapCombiner, CH_ID);
        }
        // 否则需要经过一次finisher操作，把Map<K,A>转换为M extends Map<K,D>
        else{
            @SuppressWarnings("unchecked")
            Function<A,A> downStreamFinisher = (Function<A,A>)downStream.finisher();
            Function<Map<K,A>,M> finisher = intermediate -> {
                // 把Map<K,A>当中所有的A类型的值通过downStreamFinisher转换一次
                intermediate.replaceAll((k,v) -> downStreamFinisher.apply(v));
                @SuppressWarnings("unchecked")
                M castResult = (M) intermediate;
                return castResult;
            };
            return new CollectorImpl<>(mangledFactory, accumulator, mapCombiner, finisher,CH_ID);
        }
    }

    public static <K,V,M extends Map<K,V>>
    BinaryOperator<M> mapMerger(BinaryOperator<V> combiner){
        return (left,right) -> {
            // 把right的Map当中的所有值合并到left当中
            for(Map.Entry<K,V> entry:right.entrySet()){
                // 如果已经存在值，则使用combiner组合这两个值
                left.merge(entry.getKey(),entry.getValue(),combiner);
            }
            return left;
        };
    }

    @SuppressWarnings("unchecked")
    private static <I, R> Function<I, R> castingIdentity() {
        return i -> (R) i;
    }

    private static class CollectorImpl<T,A,R> implements Collector<T,A,R>{
        private final Supplier<A> supplier;

        private final BiConsumer<A,T> accumulator;

        private final BinaryOperator<A> combiner;

        private final Function<A, R> finisher;

        private final Set<Characteristics> characteristics;

        CollectorImpl(Supplier<A> supplier,
                      BiConsumer<A,T> accumulator,
                      BinaryOperator combiner,
                      Function<A, R> finisher,
                      Set<Characteristics> characteristics){
            this.supplier = supplier;
            this.accumulator = accumulator;
            this.combiner = combiner;
            this.finisher = finisher;
            this.characteristics = characteristics;
        }

        CollectorImpl(Supplier<A> supplier,
                      BiConsumer<A, T> accumulator,
                      BinaryOperator<A> combiner,
                      Set<Characteristics> characteristics) {
            this(supplier, accumulator, combiner, castingIdentity(), characteristics);
        }

        @Override
        public Supplier<A> supplier() {
            return supplier;
        }

        @Override
        public BiConsumer<A, T> accumulator() {
            return accumulator;
        }

        @Override
        public BinaryOperator<A> combiner() {
            return combiner;
        }

        @Override
        public Function<A, R> finisher() {
            return finisher;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return characteristics;
        }
    }
}
