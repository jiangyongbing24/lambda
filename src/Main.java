import com.test.util.Collection;
import sun.misc.Unsafe;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class Main {

    public static void main(String[] args) {
        ArrayList<String> list = new ArrayList<>();
        list.add("111");
        list.add("222");
        list.add("333");
        final int length = list.size();
        String[] strs = list.toArray(String[]::new);
        list.forEach(x -> System.out.println(x));
        list.forEach(System.out::println);
        for(int i=0;i<strs.length;i++){
            System.out.println(strs[i]);
        }
     }

    public static void test(List<String> list) {
        list.add("333");
        System.out.println(list.toString());
    }
}
