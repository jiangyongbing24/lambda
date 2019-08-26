package test;

import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {
        TestNewHashMap();
    }

    /** 测试new一个HashMap的时候putMapEntries的工作流程 */
    public static void TestNewHashMap(){
        HashMap<Integer,Integer> map1 = new HashMap<>();
        for(int i = 1;i <= 12;i++){
            map1.put(i,i);
        }
        HashMap<Integer,Integer> map2 = new HashMap<>(map1);
        System.out.println(map2.size());
    }

    /** Stream流程处理的测试 */
    public static void StreamProcess(){
        Stream.of(1,2,3,15,64)
                .parallel() //并行流
                .filter(i -> i > 1) //无状态
                .map(i -> i+1) //无状态
                .skip(1) //有状态的操作
                .peek(i -> i = i+1) //无状态的
                .forEach(System.out::println);
    }

    /** 测试Map.Entry遍历Map */
    public static void TestMapEntry(){
        Map<String, String> map = new HashMap<>();
        map.put("1", "value1");
        map.put("2", "value2");
        map.put("2","test");
        map.put("3", "value3");

        Set<Map.Entry<String,String>> set = map.entrySet();//获取条目的Set
        Iterator<Map.Entry<String,String>> it = set.iterator();//获取这个Set的迭代器
        Comparator<Map.Entry<String,String>> comparator = Map.Entry.comparingByKey();//得到一个比较器
        //获取set中的元素
        Map.Entry<String,String> pre = set.stream().parallel().filter(m -> "value1".equals(m.getValue())).findFirst().orElse(null);
        while(it.hasNext()){
            Map.Entry<String,String> entry = it.next();
            System.out.println(comparator.compare(pre,entry));//比较两个元素
            System.out.println("key=" + entry.getKey() + " and value=" + entry.getValue() + " " +entry.setValue("test"));
        }
        map.replaceAll((k,v) -> k + v);
        System.out.println(map);
    }

    /** 测试Stream.Builder的应用 */
    public static void TestStreamBuilder(){
        Stream.Builder<String> builder = Stream.builder();
        builder = builder.add("a").add("b").add("c").add("d").add("e").add("f");
        Stream.Builder<String> builder1 = Stream.builder();
        builder1 = builder1.add("a").add("b").add("c").add("d").add("e").add("f");
        builder1.build();
        Spliterator<String> spliterator = (Spliterator<String>) builder1;
        spliterator.tryAdvance(System.out::println);
    }

    /** 测试Stream中的各种Match方法 */
    public static void TestStreamMatch(){
        boolean result = Stream.of("one", "two", "three", "four").noneMatch(s -> s.length() > 5);
        Stream stream = Stream.of("one", "two", "three", "four");
        System.out.println(stream.findFirst());
    }

    public static void TestRightShiftSymbol(){
        int a = -1;
        System.out.println(Test.IntegerTo32BinaryString(a));
        System.out.println(Test.IntegerTo32BinaryString(~a));
        System.out.println(Test.IntegerTo32BinaryString(a >> -3));
        System.out.println(Test.IntegerTo32BinaryString(a >>> -3));
        System.out.println(Integer.bitCount(7));
    }

    /** 测试System.arraycopy方法 */
    public static void SystemArrayCopy(){
        int[] a = new int[]{1,2,3};
        int[] b = new int[]{4,5};
        System.arraycopy(a,1,b,0,1);
        for (int i = 0; i < b.length; i++) {
            System.out.println(b[i]);
        }
    }

    /** 测试Integer.numberOfLeadingZeros()方法 */
    public static void IntegernumberOfLeadingZeros(){
        int MIN_CHUNK_POWER = 4;
        int initialCapacity = 16;
        System.out.println(Test.IntegerTo32BinaryString(initialCapacity));
        int initialChunkPower = Math.max(MIN_CHUNK_POWER
                ,Integer.SIZE - Integer.numberOfLeadingZeros(initialCapacity));
        System.out.println(initialChunkPower);
    }

    /** 测试断言 */
    public static void Assert(){
        String name = "";
        assert !"".equals(name):"名字为空";
        System.out.println(name);
    }

    public static void TestStreamCollect(){
        ArrayList<String> list = Stream.of("one", "two", "three", "four")
                .parallel()
                .collect(ArrayList::new,ArrayList::add,ArrayList::addAll);
        list.forEach(System.out::println);
    }

    /**
     * 测试流的有状态操作和无状态操作
     * */
    public static void TestStreamStatus(){
        /** 由于是并行的流，而map是一个有状态的操作，
         * 在这种情况下，每次执行的结果输出都不同
         * */
//        Set<Integer> seen = Collections.synchronizedSet(new HashSet<>());
//        seen.add(1);
//        Stream.of(1,2,3)
//                .parallel()
//                .map(e -> { if (seen.add(e)) return 0; else return e; })
//                .forEach(System.out::println);

        int[] obj = IntStream.range(0,5).parallel().map(x -> x*2).toArray();
        for(int i:obj){
            System.out.println(i);
        }
    }

    /**
     * 测试Stream接口的peek方法
     * */
    public static void TestStreamPeek(){
        Stream.of("one", "two", "three", "four")
                .filter(e -> e.length() > 3)
                .peek(e -> System.out.println("Filtered value: " + e))
                .map(String::toUpperCase)
                .peek(e -> System.out.println("Mapped value: " + e))
                .collect(Collectors.toList());

        Stream.of("one", "two", "three", "four")
                .filter(e -> e.length() > 3)
                .limit(3)
                .peek(System.out::println)
                .collect(Collectors.toList());

        String[] str = Stream.of("one", "two", "three", "four").toArray(String[]::new);
        for(int i=0;i<str.length;i++){
            System.out.print(str[i] + " ");
        }
        System.out.println();
        int sum = Stream.of(1,2,3,4).parallel().reduce(576,(x,y) -> x /y);
        System.out.println(sum);
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

    /**
     * 测试Function接口的andThen方法
     * */
    public static void TestFunction(){
        Function<Integer,Integer> function1  = x -> x * x;
        Function<Integer,Integer> function2 = function1.compose(x -> x + 2);
        Function<Integer,Integer> function3 = function2.andThen(x -> x * 10);
        System.out.println(function2.apply(4)); //36
        System.out.println(function3.apply(4)); // x -> (x+2)*(x+2)
    }
}
