import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;

public class Main {

    public static void main(String[] args) {
        int[] mans = new int[]{111,222,333,444,555,666,777,888,999};
//        System.setProperty("org.openjdk.java.util.stream.tripwire","true");
        Spliterator.OfInt spliterator = Spliterators.spliterator(mans,Spliterator.SIZED);
        TestConsumer action = new TestConsumer();
        spliterator.tryAdvance(action);
    }

    public static class TestConsumer implements Consumer<Integer> {
        @Override
        public void accept(Integer integer) {
            System.out.println(integer * integer);
        }
    }
}
