package in.rk.bank.db;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AccountDB {
    private static final Map<Integer, Integer> accountsMap = IntStream.rangeClosed(1, 10)
            .boxed()
            .collect(
                    Collectors.toMap(Function.identity(),
                            v -> v * 10)
            );

    public static boolean isAccountAvailable(int accountNumber) {
        return accountsMap.containsKey(accountNumber);
    }

    public static Integer getBalance(int accountNumber) {
        return accountsMap.getOrDefault(accountNumber, 0);
    }

    public static Integer addBalance(int accountNumber, int amount) {
        return accountsMap.computeIfPresent(accountNumber, (k, v) -> v + amount);
    }

    public static Integer deductBalance(int accountNumber, int amount) {
        return accountsMap.computeIfPresent(accountNumber, (k, v) -> v - amount);
    }

}
