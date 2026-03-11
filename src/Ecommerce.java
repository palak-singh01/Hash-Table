import java.util.*;

public class Ecommerce {

    // productId -> stockCount
    private HashMap<String, Integer> stockMap;

    // productId -> waiting list (FIFO)
    private HashMap<String, LinkedHashMap<Integer, Integer>> waitingList;

    public Ecommerce() {
        stockMap = new HashMap<>();
        waitingList = new HashMap<>();
    }

    // Add product with stock
    public void addProduct(String productId, int stock) {
        stockMap.put(productId, stock);
        waitingList.put(productId, new LinkedHashMap<>());
    }

    // Check stock availability
    public int checkStock(String productId) {
        return stockMap.getOrDefault(productId, 0);
    }

    // Purchase item (thread-safe)
    public synchronized String purchaseItem(String productId, int userId) {

        if (!stockMap.containsKey(productId)) {
            return "Product not found";
        }

        int stock = stockMap.get(productId);

        if (stock > 0) {
            stock--;
            stockMap.put(productId, stock);

            return "Success, " + stock + " units remaining";
        } else {
            LinkedHashMap<Integer, Integer> queue = waitingList.get(productId);
            int position = queue.size() + 1;
            queue.put(userId, position);

            return "Added to waiting list, position #" + position;
        }
    }

    // View waiting list
    public void showWaitingList(String productId) {
        LinkedHashMap<Integer, Integer> queue = waitingList.get(productId);

        if (queue.isEmpty()) {
            System.out.println("Waiting list empty");
            return;
        }

        for (Map.Entry<Integer, Integer> entry : queue.entrySet()) {
            System.out.println("User " + entry.getKey() +
                    " Position #" + entry.getValue());
        }
    }

    // Demo
    public static void main(String[] args) {

        Ecommerce store = new Ecommerce();

        store.addProduct("IPHONE15_256GB", 2);

        System.out.println("Stock: " + store.checkStock("IPHONE15_256GB"));

        System.out.println(store.purchaseItem("IPHONE15_256GB", 12345));
        System.out.println(store.purchaseItem("IPHONE15_256GB", 67890));
        System.out.println(store.purchaseItem("IPHONE15_256GB", 99999));

        store.showWaitingList("IPHONE15_256GB");
    }
}
