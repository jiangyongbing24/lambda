import com.test.util.Collection;
import sun.misc.Unsafe;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class Main {

    public static void main(String[] args) {
        List<String> list = new LinkedList<>();
        System.out.println(list.toString());
        test(list);
        System.out.println(list.toString());
     }

    public static void test(List<String> list) {
        list.add("333");
        System.out.println(list.toString());
    }
}
