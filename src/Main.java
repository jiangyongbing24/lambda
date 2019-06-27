import com.test.util.Collection;
import com.test.util.function.Function;
import sun.misc.Unsafe;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class Main {

    public static void main(String[] args) {
        ArrayList<String> list1 = new ArrayList<>();
        list1.add("111");
        list1.add("222");
        list1.add("333");
        ArrayList<String> list2 = new ArrayList<>();
        list2.add("444");
        list2.add("555");
        list2.add("666");
        TaggedArray<String> stringTaggedArray = new TaggedArray<String>(list1.toArray(new String[0]),list2.toArray(new String[0]));
        Spliterator<String> spliterator = stringTaggedArray.spliterator();
//        spliterator.forEachRemaining(System.out::println);
        System.out.println(spliterator.tryAdvance(System.out::println));
        System.out.println(spliterator.estimateSize());
        System.out.println(spliterator.tryAdvance(System.out::println));
        System.out.println(spliterator.estimateSize());
        System.out.println(spliterator.tryAdvance(System.out::println));
        System.out.println(spliterator.estimateSize());
     }

    public static void test(List<String> list) {
        list.add("333");
        System.out.println(list.toString());
    }

}
