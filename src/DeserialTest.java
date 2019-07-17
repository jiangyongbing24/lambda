import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * @Created by JYB
 * @Date 2019/7/11 20:54
 * @Description TODO
 */
public class DeserialTest {
    public static void main(String[] args) {
        Person p;
        try{
            FileInputStream fis = new FileInputStream("Person.txt");
            ObjectInputStream ois = new ObjectInputStream(fis);
            p = (Person)ois.readObject();
            ois.close();
            System.out.println(p.toString());
        }catch (IOException | ClassNotFoundException ex){
            ex.printStackTrace();
        }
    }
}
