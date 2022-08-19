package top.codingoer;

import java.time.LocalDate;

/**
 * Description：
 *
 * @author Lionel
 * @date Created in 2022/7/28 16:24
 */
public class LocalDateTest {

    public static void main(String[] args) {
        LocalDate expiryDate = LocalDate.of(2022, 7, 27);
        LocalDate effectDate = LocalDate.of(2021, 9, 4);
        int actualContractTerm = (int) (expiryDate.toEpochDay() - effectDate.toEpochDay()) / 30;
        System.out.println(actualContractTerm);
    }
}
