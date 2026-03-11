import java.util.*;

public class DNSCache {

    // Entry class
    static class DNSEntry {
        String ipAddress;
        long expiryTime;

        DNSEntry(String ipAddress, int ttlSeconds) {
            this.ipAddress = ipAddress;
            this.expiryTime = System.currentTimeMillis() + ttlSeconds * 1000;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }

    private int capacity;
    private LinkedHashMap<String, DNSEntry> cache;
    private int hits = 0;
    private int misses = 0;

    public DNSCache(int capacity) {

        this.capacity = capacity;

        cache = new LinkedHashMap<String, DNSEntry>(capacity, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                return size() > DNSCache.this.capacity;
            }
        };

        startCleanupThread();
    }

    // Resolve domain
    public synchronized String resolve(String domain) {

        DNSEntry entry = cache.get(domain);

        if (entry != null && !entry.isExpired()) {
            hits++;
            return entry.ipAddress;
        }

        if (entry != null && entry.isExpired()) {
            cache.remove(domain);
        }

        misses++;

        String ip = queryUpstreamDNS(domain);

        cache.put(domain, new DNSEntry(ip, 300));

        return ip;
    }

    // Simulated upstream DNS query
    private String queryUpstreamDNS(String domain) {
        Random rand = new Random();
        return "172.217.14." + (rand.nextInt(200) + 1);
    }

    // Cache statistics
    public void getCacheStats() {
        int total = hits + misses;
        double hitRate = total == 0 ? 0 : (hits * 100.0) / total;

        System.out.println("Hits: " + hits);
        System.out.println("Misses: " + misses);
        System.out.println("Hit Rate: " + hitRate + "%");
    }

    // Background cleanup thread
    private void startCleanupThread() {

        Thread cleaner = new Thread(() -> {

            while (true) {

                try {
                    Thread.sleep(5000);

                    synchronized (this) {
                        Iterator<Map.Entry<String, DNSEntry>> it = cache.entrySet().iterator();

                        while (it.hasNext()) {
                            Map.Entry<String, DNSEntry> entry = it.next();

                            if (entry.getValue().isExpired()) {
                                it.remove();
                            }
                        }
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });

        cleaner.setDaemon(true);
        cleaner.start();
    }

    // Test
    public static void main(String[] args) {

        DNSCache dns = new DNSCache(5);

        System.out.println(dns.resolve("google.com"));
        System.out.println(dns.resolve("google.com"));
        System.out.println(dns.resolve("github.com"));

        dns.getCacheStats();
    }
}