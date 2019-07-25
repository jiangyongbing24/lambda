package test;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {
        StreamMap_StreamFlatMap();
    }

    /**
     * 测试Stream.Map和Stream.flatMap的区别
     * */
    public static void StreamMap_StreamFlatMap(){
        //直接初始化List的第一种方式
        List<String> collected = new CopyOnWriteArrayList<>(Arrays.asList("one","two","three","four","five","six"));
        //直接初始化List的第二种方式
        final List<String> collect = Stream.of("one","two","three","four","five","six").collect(Collectors.toList());
        //把Stream<String>流转换成Stream<Integer>流
        List<Integer> figure = collected.stream().map(s -> collect.indexOf(s)).collect(Collectors.toList());
        figure.forEach(s -> System.out.print(s + " "));
        System.out.println();
        //直接初始化List的第三种方式
        List<Integer> a = Arrays.asList(1,2,3);
        List<Integer> b = Arrays.asList(4,5,6);
        //Stream.of(a,b)生成一个Stream<List<Integer>>的流，然后再把这个流转换为多个Stream<Integer>流，然后再聚合多个Stream<Integer>流
        List<Integer> c = Stream.of(a,b).flatMap(s -> s.stream()).collect(Collectors.toList());
//        List<List<Integer>> c = Stream.of(a,b).collect(Collectors.toList());
        c.forEach(s -> System.out.print(s + " "));
    }

    /**
     * 尝试Collection中的一些方法
     * */
    public static void TestClMethod(){
        ArrayList<String> list1 = new ArrayList<>();
        list1.add("张三");
        list1.add("李四");
        list1.add("王麻子");
        ArrayList<String> list2 = new ArrayList<>();
        list2.add("王麻子");
        list2.add("汤姆");
//        list1.retainAll(list2);
        list1.clear();
        list1.forEach(System.out::println);
    }

    /**
     * 测试Collection中把集合转换为数组的方法 T[] toArray(T[] a)
     **/
    public static void TestCollection(){
        String[] mans = new String[]{"张三","李四","王麻子","汤姆","杰瑞"};
        ArrayList<String> manList = new ArrayList<>();
        Collections.addAll(manList,mans);
        Object[] tests = manList.toArray(new Object[0]);
        for (Object str:tests){
            System.out.print(str.toString() + " ");
        }
        System.out.println();
        boolean result = manList.removeIf(s -> {
            if(s.length() > 12)
                return true;
            return false;
        });
        manList.forEach(System.out::println);
        System.out.println(result);
    }

    /**
     * 尝试Spliterators.iterator(spliterator)方法，把Spliterator适配为Iterator
     * */
    public static void TestSpliteratorsIterator(){
        String[] mans = new String[]{"张三","李四","王麻子","汤姆","杰瑞"};
        ArrayList<String> manList = new ArrayList<>();
        Collections.addAll(manList,mans);
        Spliterator<String> spliterator = manList.spliterator();
        spliterator.tryAdvance(System.out::println);
        System.out.println();
//        spliterator = spliterator.trySplit();
        Iterator<String> iterator = Spliterators.iterator(spliterator);
        iterator.forEachRemaining(System.out::println);
    }

    /**
     * 尝试返回集合迭代器的Spliterators.spliterator();
     * */
    public static void TestCollectionSpliterator(){
//        String[] mans = new String[]{"张三","李四","王麻子","汤姆","杰瑞"};
//        ArrayList<String> manList = new ArrayList<>();
//        Collections.addAll(manList,mans);
//        for (int i = 0;i < 4096 * 2; i++){
//            manList.add(Integer.toString(i));
//        }
//        Spliterator<String> spliterator = Spliterators.spliterator(manList,Spliterator.SIZED);
//        Spliterator<String> spliterator = Spliterators.spliterator(manList.iterator(),manList.size(),Spliterator.SIZED);
//        Spliterator<String> spliterator1 = spliterator.trySplit();
//        Spliterator<String> spliterator2 = spliterator.trySplit();
//        Spliterator<String> spliterator3 = spliterator.trySplit();
//        Spliterator<String> spliterator4 = spliterator.trySplit();
//        System.out.println(spliterator1.estimateSize());
//        System.out.println(spliterator2.estimateSize());
//        System.out.println(spliterator3.estimateSize());
//        System.out.println(spliterator4.estimateSize());
//        System.out.println(spliterator.estimateSize());
    }

    /**
     * 尝试Tripwire在Spliterators.IntArraySpliterator.tryAdvance中的作用
     * */
    public static void TestTripwire(){
        int[] mans = new int[]{111,222,333,444,555,666,777,888,999};
        System.setProperty("org.openjdk.java.util.stream.tripwire","true");
        Spliterator.OfInt spliterator = Spliterators.spliterator(mans,Spliterator.SIZED);
        TestConsumer action = new TestConsumer();
        spliterator.tryAdvance(action);
    }

    public static class TestConsumer implements Consumer<Integer> {
        @Override
        public void accept(Integer integer) {
            System.out.println(integer * integer);
        }
    }

    public static void TestFunction(){
        Function<Integer,Integer> function1  = x -> x * x;
        Function<Integer,Integer> function2 = function1.compose(x -> x + 2);
        Function<Integer,Integer> function3 = function2.andThen(x -> x * 10);
        System.out.println(function2.apply(4)); //36
        System.out.println(function3.apply(4)); // x -> (x+2)*(x+2)
    }
}
