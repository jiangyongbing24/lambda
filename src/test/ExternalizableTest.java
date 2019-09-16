package test;

import javax.xml.ws.soap.Addressing;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;

/**
 * 当使用该接口时，序列化的细节需要由程序员去完成。
 * 如果writeExternal()与readExternal()方法未作任何处理，
 * 那么该序列化行为将不会保存/读取任何一个字段
 *
 * 另外，若使用Externalizable进行序列化，当读取对象时，
 * 会调用被序列化类的无参构造器去创建一个新的对象，
 * 然后再将被保存对象的字段的值分别填充到新对象中
 * */
@Addressing
public class ExternalizableTest implements Externalizable {
    private String field1;

    private String field2;

    public ExternalizableTest(){
        System.out.println("构造方法");
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(field1);
        out.writeUTF(field2);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.field1 = in.readUTF();
        this.field2 = in.readUTF();
    }

    public static void main(String[] args) {
        ExternalizableTest externalizableTest = new ExternalizableTest();
        externalizableTest.field1 = "1";
        externalizableTest.field2 = "2";
        try{
            ObjectOutputStream ops = new ObjectOutputStream(
                    new FileOutputStream("E:\\IdeaWorkSpace\\lambda\\EnternalizableTest"));
            ops.writeObject(externalizableTest);
            ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream("E:\\IdeaWorkSpace\\lambda\\EnternalizableTest"));
            ExternalizableTest test = (ExternalizableTest) ois.readObject();
            Annotation[] annotations =  externalizableTest.getClass().getAnnotations();
            for (Annotation annotation:annotations) {
                System.out.println(annotation.getClass().getName());
            }
            System.out.println(test.field1);
            System.out.println(test.field2);
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch(ClassNotFoundException e){
            e.printStackTrace();
        }

    }

    @interface Test{
    }
}
