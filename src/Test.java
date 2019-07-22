
import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/**
 * @Created by JYB
 * @Date 2019/7/14 21:40
 * @Description TODO
 */
public class Test {
    public static void main(String[] args) {
        String[] mans = new String[]{"张三","李四","王麻子","汤姆","杰瑞"};
        ArrayList<String> manList = new ArrayList<>();
        Collections.addAll(manList,mans);
        Spliterator<String> spliterator = Spliterators.spliterator(mans,Spliterator.DISTINCT);
//        Spliterator<String> spliterator = manList.spliterator();
        spliterator.tryAdvance(System.out::println);
        System.out.println("==========================");
        spliterator = spliterator.trySplit();
        spliterator.tryAdvance(System.out::println);
//        spliterator.forEachRemaining(System.out::println);
        Consumer<Integer> action = x -> {
            System.out.println(x * x);
        };
        funTest(x -> {
            System.out.println(x * x);
        });
        funTest(action::accept);
//        System.out.println(IntegerTo32BinaryString(~1));
//        System.out.println(IntegerTo32BinaryString(1));
//        System.out.println(IntegerTo32BinaryString(4));
//        System.out.println(IntegerTo32BinaryString(16));
//        System.out.println(IntegerTo32BinaryString(64));
//        System.out.println(IntegerTo32BinaryString(256));
//        System.out.println(IntegerTo32BinaryString(1024));
//        System.out.println(IntegerTo32BinaryString(4096));
//        System.out.println(IntegerTo32BinaryString(16384));
//        System.out.println(IntegerTo32BinaryString(-16384 >> 2));
    }

    public static void funTest(IntConsumer fun){
//        fun.accept(5);
    }

    public static String IntegerTo32BinaryString(int num){
        String str = Integer.toBinaryString(num);
        char[] temp = str.toCharArray();
        char[] chars = new char[32];
        for(int i = 0;i < chars.length;i++){
            chars[i] = '0';
        }
        for(int i = temp.length - 1;i >= 0 ;i--){
            chars[32 - (temp.length - i)] = temp[i];
        }
        return new String(chars);
    }
}
