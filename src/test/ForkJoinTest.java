package test;

import java.util.concurrent.*;
import java.util.stream.LongStream;

/**
 * Forkjoin框架的使用
 * */
public class ForkJoinTest {
    public static void main( String[] args ) throws InterruptedException {
        ExecutorService pool = new ThreadPoolExecutor(10,10,
                1, TimeUnit.SECONDS,new LinkedBlockingDeque<>(),
                Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());
        for (int i = 0; i < 20; i++) {
            final int a = i;
            pool.execute(() -> {
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(a);
            });
        }
        pool.awaitTermination(20,TimeUnit.SECONDS);
        pool.shutdownNow();
    }
}
