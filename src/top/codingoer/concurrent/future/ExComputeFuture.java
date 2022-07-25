package top.codingoer.concurrent.future;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Description：
 *
 * @author Lionel
 * @date Created in 2022/7/25 17:36
 */
public class ExComputeFuture {

    private static ExecutorService executorService = Executors.newFixedThreadPool(5);

    public static void main(String[] args) {
        List<CompletableFuture> futureList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(new Task(i), executorService);
            futureList.add(voidCompletableFuture);
        }

        CompletableFuture[] futures = new CompletableFuture[futureList.size()];
        for (CompletableFuture future : futures) {
            future.join();
        }
        CompletableFuture.anyOf(futureList.toArray(futures));

        System.out.println("method end");
        executorService.shutdown();
    }

    public static class Task implements Runnable {

        private final int i;

        public Task(int i) {
            this.i = i;
        }

        @Override
        public void run() {
            System.out.println(i);
            try {
                TimeUnit.SECONDS.sleep(i * 2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (i == 3) {
                throw new NullPointerException();
            }

            System.out.println(i + "end");
        }
    }
}
