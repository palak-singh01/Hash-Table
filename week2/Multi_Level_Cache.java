import java.util.*;

public class Multi_Level_Cache {

    // ------------------ Video Data ------------------
    static class Video {
        String id;
        String content;

        public Video(String id, String content) {
            this.id = id;
            this.content = content;
        }
    }

    // ------------------ LRU Cache using LinkedHashMap ------------------
    static class LRUCache<K, V> extends LinkedHashMap<K, V> {
        private int capacity;

        public LRUCache(int capacity) {
            super(capacity, 0.75f, true); // access-order = true
            this.capacity = capacity;
        }

        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > capacity;
        }
    }

    // ------------------ Caches ------------------
    private LRUCache<String, Video> L1; // Memory
    private LRUCache<String, Video> L2; // SSD (simulated)
    private Map<String, Video> L3;      // Database

    // Access count tracking
    private Map<String, Integer> accessCount;

    // Stats
    private int L1_hits = 0, L2_hits = 0, L3_hits = 0;
    private int totalRequests = 0;

    public Multi_Level_Cache() {
        L1 = new LRUCache<>(10000);
        L2 = new LRUCache<>(100000);
        L3 = new HashMap<>();
        accessCount = new HashMap<>();

        // Preload L3 (Database)
        for (int i = 1; i <= 200000; i++) {
            String id = "video_" + i;
            L3.put(id, new Video(id, "Content of " + id));
        }
    }

    // ------------------ Get Video ------------------
    public Video getVideo(String id) {
        totalRequests++;

        // L1 Check
        if (L1.containsKey(id)) {
            L1_hits++;
            updateAccess(id);
            System.out.println("L1 HIT (0.5ms)");
            return L1.get(id);
        }

        System.out.println("L1 MISS");

        // L2 Check
        if (L2.containsKey(id)) {
            L2_hits++;
            updateAccess(id);
            System.out.println("L2 HIT (5ms)");

            // Promote to L1
            promoteToL1(id, L2.get(id));

            return L2.get(id);
        }

        System.out.println("L2 MISS");

        // L3 (Database)
        if (L3.containsKey(id)) {
            L3_hits++;
            updateAccess(id);
            System.out.println("L3 HIT (150ms)");

            Video v = L3.get(id);

            // Add to L2 first
            L2.put(id, v);

            return v;
        }

        return null;
    }

    // ------------------ Access Tracking ------------------
    private void updateAccess(String id) {
        accessCount.put(id, accessCount.getOrDefault(id, 0) + 1);

        // Promote if frequently accessed
        if (accessCount.get(id) > 3 && L2.containsKey(id)) {
            promoteToL1(id, L2.get(id));
        }
    }

    private void promoteToL1(String id, Video v) {
        L1.put(id, v);
    }

    // ------------------ Invalidate Cache ------------------
    public void invalidate(String id) {
        L1.remove(id);
        L2.remove(id);
        L3.remove(id);
        accessCount.remove(id);
    }

    // ------------------ Statistics ------------------
    public void getStatistics() {
        double L1_rate = (L1_hits * 100.0) / totalRequests;
        double L2_rate = (L2_hits * 100.0) / totalRequests;
        double L3_rate = (L3_hits * 100.0) / totalRequests;

        System.out.println("\nCache Statistics:");
        System.out.println("L1 Hit Rate: " + String.format("%.2f", L1_rate) + "% (0.5ms)");
        System.out.println("L2 Hit Rate: " + String.format("%.2f", L2_rate) + "% (5ms)");
        System.out.println("L3 Hit Rate: " + String.format("%.2f", L3_rate) + "% (150ms)");

        double overall = L1_rate + L2_rate + L3_rate;
        System.out.println("Overall Hit Rate: " + String.format("%.2f", overall) + "%");
    }

    // ------------------ MAIN ------------------
    public static void main(String[] args) {

        Multi_Level_Cache cache = new Multi_Level_Cache();

        cache.getVideo("video_123");  // L3 → L2
        cache.getVideo("video_123");  // L2 → L1
        cache.getVideo("video_123");  // L1

        cache.getVideo("video_999");  // L3

        cache.getStatistics();
    }
}