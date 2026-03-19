import java.util.*;

public class two_sum {

    // Transaction Class
    static class Transaction {
        int id;
        int amount;
        String merchant;
        String account;
        long time; // store as epoch milliseconds

        public Transaction(int id, int amount, String merchant, String account, long time) {
            this.id = id;
            this.amount = amount;
            this.merchant = merchant;
            this.account = account;
            this.time = time;
        }
    }

    // ------------------ 1. Classic Two-Sum ------------------
    public static List<String> findTwoSum(List<Transaction> transactions, int target) {
        Map<Integer, Transaction> map = new HashMap<>();
        List<String> result = new ArrayList<>();

        for (Transaction t : transactions) {
            int complement = target - t.amount;

            if (map.containsKey(complement)) {
                result.add("(" + map.get(complement).id + ", " + t.id + ")");
            }
            map.put(t.amount, t);
        }
        return result;
    }

    // ------------------ 2. Two-Sum with Time Window ------------------
    public static List<String> findTwoSumWithTime(List<Transaction> transactions, int target, long windowMillis) {
        List<String> result = new ArrayList<>();
        Map<Integer, List<Transaction>> map = new HashMap<>();

        for (Transaction t : transactions) {
            int complement = target - t.amount;

            if (map.containsKey(complement)) {
                for (Transaction prev : map.get(complement)) {
                    if (Math.abs(t.time - prev.time) <= windowMillis) {
                        result.add("(" + prev.id + ", " + t.id + ")");
                    }
                }
            }

            map.putIfAbsent(t.amount, new ArrayList<>());
            map.get(t.amount).add(t);
        }
        return result;
    }

    // ------------------ 3. K-Sum ------------------
    public static List<List<Integer>> findKSum(List<Transaction> transactions, int k, int target) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(transactions, k, target, 0, new ArrayList<>(), result);
        return result;
    }

    private static void backtrack(List<Transaction> trans, int k, int target, int start,
                                  List<Integer> temp, List<List<Integer>> result) {
        if (k == 0 && target == 0) {
            result.add(new ArrayList<>(temp));
            return;
        }

        if (k == 0 || target < 0) return;

        for (int i = start; i < trans.size(); i++) {
            temp.add(trans.get(i).id);
            backtrack(trans, k - 1, target - trans.get(i).amount, i + 1, temp, result);
            temp.remove(temp.size() - 1);
        }
    }

    // ------------------ 4. Duplicate Detection ------------------
    public static List<String> detectDuplicates(List<Transaction> transactions) {
        Map<String, Set<String>> map = new HashMap<>();
        List<String> result = new ArrayList<>();

        for (Transaction t : transactions) {
            String key = t.amount + "-" + t.merchant;

            map.putIfAbsent(key, new HashSet<>());
            map.get(key).add(t.account);
        }

        for (String key : map.keySet()) {
            Set<String> accounts = map.get(key);
            if (accounts.size() > 1) {
                result.add("Duplicate: " + key + " Accounts: " + accounts);
            }
        }
        return result;
    }

    // ------------------ MAIN METHOD ------------------
    public static void main(String[] args) {

        List<Transaction> transactions = new ArrayList<>();

        long now = System.currentTimeMillis();

        transactions.add(new Transaction(1, 500, "Store A", "acc1", now));
        transactions.add(new Transaction(2, 300, "Store B", "acc2", now + 1000 * 60 * 15));
        transactions.add(new Transaction(3, 200, "Store C", "acc3", now + 1000 * 60 * 30));
        transactions.add(new Transaction(4, 500, "Store A", "acc4", now + 1000 * 60 * 45));

        // 1. Two Sum
        System.out.println("Two Sum:");
        System.out.println(findTwoSum(transactions, 500));

        // 2. Two Sum with 1-hour window
        System.out.println("\nTwo Sum with Time Window:");
        System.out.println(findTwoSumWithTime(transactions, 500, 3600 * 1000));

        // 3. K-Sum
        System.out.println("\nK Sum:");
        System.out.println(findKSum(transactions, 3, 1000));

        // 4. Duplicate Detection
        System.out.println("\nDuplicates:");
        System.out.println(detectDuplicates(transactions));
    }
}