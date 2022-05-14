package top.codingoer.concurrent.future.utils;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Description：
 *
 * @author Lionel
 * @date Created in 2022/5/14 23:31
 */
public class FutureUtils {

    public static <T> T get(Future<T> future) {
        try {
            return future == null ? null : future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }
}
