package test;

import java.io.*;
import java.util.Calendar;
import java.util.Date;

/**
 * 测试代理序列化，一个在序列化期间攻击源流的类
 *
 * 如果MutablePeriod.period是采用不是代理序列化的Period1
 * 则会出现 end > start的情况
 * 如果MutablePeriod.period是采用代理序列化的Period
 * 则不会出现 end > start的情况
 * */
public class MutablePeriod {
    public final Date start;
    public final Date end;
    public final Period1 period;

    /**
     * 尽管readObject中增加了有效性检查，但通过伪造字节流创建可变的Period实例仍是可能
     * 做法是：字节流以Period实例开头，然后附加上两个额外的引用执行Period实例中两个私有的Date域
     */
    public MutablePeriod() throws IOException, ClassNotFoundException{
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        // 序列化
        oos.writeObject(new Period1(new Date(), new Date()));
        // 一个额外的私有域
        // java序列化规范中，0x71代表引用，0x7e代表枚举
        byte[] ref = {0x71, 0 , 0x7e, 0, 5};
        //额外的引用，指向period中私有域start的字节
        bos.write(ref);
        ref[4] = 4;
        //额外的引用，指向period中私有域end的字节
        bos.write(ref);
        // 攻击者从ObjectInputStream中读取Period实例，然后读取附加在后面的“恶意编制对象引用Date”
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
        //把反序列化后的对象赋给属性period
        period = (Period1)ois.readObject();
        /**
         * 使 MutablePeriod 对象中的 Date start; Date end;
         * 与 Period 对象中的 Date start; Date end; 建立起关系，
         * 使MutablePeriod中的两个Date引用指向Period中的Date域
         *
         * 这样的话，只要改变了 MutablePeriod 中 Date 的值，
         * 也就是改变了 Period 中 Date 的值
         * */
        start = (Date)ois.readObject();
        end = (Date)ois.readObject();
    }

    public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
        MutablePeriod mp = new MutablePeriod();
        Period1 p = mp.period;
        Date start = mp.start;
        Date end = mp.end;

        end.setYear(78);//修改end

        System.out.println(p);
    }
}
