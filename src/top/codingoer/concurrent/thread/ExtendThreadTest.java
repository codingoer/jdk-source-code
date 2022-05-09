package top.codingoer.concurrent.thread;

/**
 * Description：线程测试类
 *
 * @author Lionel
 * @date Created in 2022/5/10 00:25
 */
public class ExtendThreadTest extends Thread {

    @Override
    public void run() {
        System.out.println("thread" + Thread.currentThread().getName());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("run end");
    }

    public static void main(String[] args) {
        new ExtendThreadTest().start();
        System.out.println("main end");
    }
}
