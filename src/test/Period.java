package test;

import java.io.*;
import java.util.Date;

/**
 * 测试序列化代理
 *
 */
public class Period implements Serializable {
    private static final long serialVersionUID = 1L;
    //开始时间
    private final Date start;
    //结束时间
    private final Date end;

    public Period(Date start, Date end) {
        if(null == start || null == end || start.after(end)){
            throw new IllegalArgumentException("请传入正确的时间区间!");
        }
        this.start = start;
        this.end = end;
    }

    public Date start(){
        return new Date(start.getTime());
    }

    public Date end(){
        return new Date(end.getTime());
    }

    @Override
    public String toString(){
        return "起始时间：" + start + " , 结束时间：" + end;
    }

    /**
     * 序列化类时，jvm 会转到这个方法，最后其实是序列化了一个内部的代理类对象！
     * */
    private Object writeReplace(){
        System.out.println("进入了writeReplace了");
        return new SerializabtionProxy(this);
    }

    /**
     * 如果攻击者伪造了一个字节码文件，然后来反序列化也无法成功，因为外围类的readObject方法直接抛异常！
     */
    private void readObject(ObjectInputStream ois) throws InvalidObjectException {
        throw new InvalidObjectException("Proxy required!");
    }

    /** Period的代理序列化类 */
    private static class SerializabtionProxy implements Serializable{
        private static final long serialVersionUID = 1L;
        private final Date start;
        private final Date end;

        //通过Period初始化代理类
        SerializabtionProxy(Period p){
            this.start = p.start;
            this.end = p.end;
        }

        /**
         * 反序列化这个类时，虚拟机会调用这个方法，最后返回的对象是一个Period对象！这里同样调用了Period的构造函数，
         * 会进行构造函数的一些校验！
         */
        private Object readResolve(){
            System.out.println("进入readResolve()方法，将返回Period对象！");
            // 这里进行保护性拷贝！
            return new Period(new Date(start.getTime()), new Date(end.getTime()));
        }
    }

    //测试序列化代理
    public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException{
        Period period = new Period(new Date(), new Date());
        System.out.println("序列化前：" + period);
        ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(
                        new File("E:\\IdeaWorkSpace\\lambda\\Period.data")));
        oos.writeObject(period);
        ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(
                        new File("E:\\IdeaWorkSpace\\lambda\\Period.data")));
        Period newPeriod = (Period)ois.readObject();
        System.out.println("序列化后：" + newPeriod);
    }
}
