import java.util.ArrayList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by SegFault on 16/01/2017.
 */
public class Test {
    public static Random random = new Random();

    private static class Producer extends Thread {
        private Queue<String> queue;
        private int n;

        public Producer(int n, Queue<String> queue) {
            this.n = n;
            this.queue = queue;
        }

        public void run() {
            while (true) {
                String r = getRandomString();
                System.out.println("prod" + n + ": " + r);
                queue.add(r);
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private static String getRandomString() {
            int l = random.nextInt(5) + 3;
            String s = "";
            for (int i = 0; i < l; i++) {
                s += (char)(random.nextInt(26) + 'a');
            }
            return s;
        }
    }

    private static class Consumer extends Thread {
        private Queue<String> queue;
        private int n;

        public Consumer(int n, Queue<String> queue) {
            this.n = n;
            this.queue = queue;
        }

        public void run() {
            while (true) {
                String s;
                while ((s = queue.poll()) != null) {
                    System.out.println("cons" + n + ": " + s.toUpperCase());
                }
            }
        }
    }

    private static class Manager {
        Queue<String> queue = new LinkedBlockingDeque<>();

        public Manager() {
            Executor executor = Executors.newCachedThreadPool();
            for (int i = 0; i < 500; i++) {
                final int n = i;
                executor.execute(() -> {
                    new Producer(n, queue).start();
                });
                executor.execute(() -> {
                    new Consumer(n, queue).start();
                });
            }
        }
    }

    public static void main(String[] args) {
        new Manager();
    }
}
