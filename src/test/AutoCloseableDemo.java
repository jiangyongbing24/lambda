package test;

/**
 * @Created by JYB
 * @Date 2019/7/24 20:04
 * @Description TODO
 */
public class AutoCloseableDemo {
    public static void main(String[] args) {
        try(AutoCloseableApp app = new AutoCloseableApp()){
            System.out.println("执行main方法");
        }catch (Exception e){
            String a = null;
            System.out.println("捕获到异常");
        }
    }

    public static class AutoCloseableApp implements AutoCloseable{
        @Override
        public void close() throws Exception {
            System.out.println("===close===");
        }
    }
}
