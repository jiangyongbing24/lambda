import com.sun.org.apache.xpath.internal.operations.String;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: JYB
 * @Date: 2019/6/25 13:56
 * @Description:
 */
public class Addresser {
    private static Unsafe unsafe;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static long addressOf(Object o) throws Exception {

        Object[] array = new Object[] { o };

        long baseOffset = unsafe.arrayBaseOffset(Object[].class);
        //arrayBaseOffset方法是一个本地方法，可以获取数组第一个元素的偏移地址
        int addressSize = unsafe.addressSize();
        long objectAddress;
        switch (addressSize) {
            case 4:
                objectAddress = unsafe.getInt(array, baseOffset);

                break;
            case 8:
                objectAddress = unsafe.getLong(array, baseOffset);
                //getLong方法获取对象中offset偏移地址对应的long型field的值
                break;
            default:
                throw new Error("unsupported address size: " + addressSize);
        }
        return (objectAddress);
    }

    public static void main(java.lang.String[] args) throws Exception {
//        Object mine = "Hello world".toCharArray(); //先把字符串转化为数组对象
//        long address = addressOf(mine);
//        System.out.println("Addess: " + address);

        List<String> list = new ArrayList<>();
        System.out.println(addressOf(list));
        test(list);
        // Verify address works - should see the characters in the array in the output
//        printBytes(address, 27);
    }

    public static void test(List<String> list) throws Exception{
        System.out.println(addressOf(list));
    }

    public static void printBytes(long objectAddress, int num) {
        for (long i = 0; i < num; i++) {
            int cur = unsafe.getByte(objectAddress + i);
            System.out.print((char) cur);
        }
        System.out.println();
    }
}
