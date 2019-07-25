package test;

import java.io.Serializable;

/**
 * @Created by JYB
 * @Date 2019/7/24 20:03
 * @Description TODO
 */
public class Person extends Man implements Serializable {
//    private static final long serialVersionUID = 4359709211352400087L;

    public int id;
    public String name;
    public transient int age;

    public Person(long weight){
        super(weight);
    }

    public Person(
            long weight,
            int id
            ,String name
            ,int age
    ){
        this.weight = weight;
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public String toString() {
        return
                "id:" + id
                        + " name:" + name.toString()
                        + " age:" + age
                ;
    }
}