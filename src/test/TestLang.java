package test;

import com.test.util.EnumSet;
import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;

/**
 * 测试 SharedSecrets.getJavaLangAccess()方法
 */
public class TestLang {
    public static void main(String[] args) {
        JavaLangAccess access = SharedSecrets.getJavaLangAccess();
        Throwable throwable = new Throwable();

        int depth = access.getStackTraceDepth(throwable);

        //输出JVM栈帧中的所有类实例
        for (int i = 0; i < depth; i++) {
            StackTraceElement frame = access.getStackTraceElement(throwable, i);
            System.out.println(frame);
        }

        Enum[] enums = access.getEnumConstantsShared(TestEnum.class);
        for (int i=0;i<enums.length;i++){
            System.out.println(enums[i]);
        }
    }

    enum TestEnum{
        one,two,three;
    }
}
