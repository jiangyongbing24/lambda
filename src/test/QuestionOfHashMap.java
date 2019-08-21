package test;

import com.test.util.Arrays;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 探究一个问题，如下所以HashMap的定义：
 * HashMap<K,V> extends AbstractMap<K,V> implements Map<K,V>, Cloneable, Serializable
 * 为什么HashMap继承了AbstractMap又去实现Map接口？？？？
 * 正确的回答就是，这就是一个Josh Bloch(HashMap的作者)编码时候的疏忽，是一个错误:
 * https://stackoverflow.com/questions/2165204/why-does-linkedhashsete-extend-hashsete-and-implement-sete
 * 但是有人提出，可能在使用Class的getInterfaces可以的得到Map.class，
 * */
public class QuestionOfHashMap {
    //定义一个测试的接口
    public static interface MyInterface {
        void foo();
    }

    //测试接口的实现
    public static class BaseClass implements MyInterface, Cloneable, Serializable {

        @Override
        public void foo() {
            System.out.println("BaseClass.foo");
        }
    }

    //继承了测试接口的默认实现，但是没有implements接口MyInterface
    public static class Class1 extends BaseClass {

        @Override
        public void foo() {
            super.foo();
            System.out.println("Class1.foo");
        }
    }

    //继承了测试接口的默认实现，并且implements接口MyInterface
    static class Class2 extends BaseClass implements MyInterface, Cloneable,
            Serializable {

        @Override
        public void foo() {
            super.foo();
            System.out.println("Class2.foo");
        }
    }

    public static void main(String[] args) {
        MyInterface c1 = new Class1();
        MyInterface c2 = new Class2();

        // 动态代理成功
        MyInterface proxy2 = createProxy(c2);
        proxy2.foo();

        // 动态代理失败
        MyInterface proxy1 = createProxy(c1);
        proxy1.foo();
    }

    private static void showInterfacesFor(Class<?> clazz) {
        System.out.printf("%s --> %s\n", clazz, Arrays.toString(clazz
                .getInterfaces()));
    }

    private static <T> T createProxy(final T obj) {
        return (T) Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj
                .getClass().getInterfaces(), (proxy,method,args) -> {
            System.out.printf("在 %s 上调用方法 %s()\n", obj,method.getName());
            return method.invoke(obj, args);
        });
    }
}

