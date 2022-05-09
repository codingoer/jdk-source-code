package top.codingoer.collection;

import java.util.HashMap;

/**
 * Description：HashMap测试类
 *
 * @author Lionel
 * @date Created in 2022/5/10 00:22
 */
public class HashMapTest {

    public static void main(String[] args) {
        HashMap<String, Double> hashMapTest = new HashMap();

        hashMapTest.put("0.1", 0.1);
        hashMapTest.put("0.2", 0.2);

        for (String s : hashMapTest.keySet()) {
            System.out.println(s);
        }

    }
}
