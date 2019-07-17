import com.test.util.Collection;
import com.test.util.function.Function;
import sun.misc.Unsafe;

import java.lang.reflect.Array;
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
        list1.addAll(list2);
        list1.spliterator();
        spliterator = list1.spliterator();
        spliterator.forEachRemaining(System.out::println);
//        System.out.println(spliterator.tryAdvance(System.out::println));
//        System.out.println(spliterator.estimateSize());
        if(spliterator.hasCharacteristics(Spliterator.ORDERED)){
            System.out.println("ORDERED");
        }
        if(spliterator.hasCharacteristics(Spliterator.DISTINCT)){
            System.out.println("DISTINCT");
        }
        if(spliterator.hasCharacteristics(Spliterator.SORTED)){
            System.out.println("SORTED");
        }
        if(spliterator.hasCharacteristics(Spliterator.SIZED)){
            System.out.println("SIZED");
        }

        if(spliterator.hasCharacteristics(Spliterator.CONCURRENT)){
            System.out.println("CONCURRENT");
        }
        if(spliterator.hasCharacteristics(Spliterator.IMMUTABLE)){
            System.out.println("IMMUTABLE");
        }
        if(spliterator.hasCharacteristics(Spliterator.NONNULL)){
            System.out.println("NONNULL");
        }
        if(spliterator.hasCharacteristics(Spliterator.SUBSIZED)){
            System.out.println("SUBSIZED");
        }
     }

    public static void test(List<String> list) {
        list.add("333");
        System.out.println(list.toString());
    }

}
