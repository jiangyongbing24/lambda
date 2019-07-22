import com.test.util.Spliterator;
import com.test.util.Spliterators;
import com.test.util.function.Consumer;

import java.util.Iterator;

public class Main {

    public static void main(String[] args) {
        TestSpliteratorsIterator();
    }

    public static void TestSpliteratorsIterator(){
        String[] mans = new String[]{"张三","李四","王麻子","汤姆","杰瑞"};
//        ArrayList<String> manList = new ArrayList<>();
//        Collections.addAll(manList,mans);
//        Spliterator<String> spliterator = manList.spliterator();
        Spliterator<String> spliterator = Spliterators.spliterator(mans,Spliterator.SIZED);
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
}
