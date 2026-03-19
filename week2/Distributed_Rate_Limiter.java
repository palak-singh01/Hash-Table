import java.util.concurrent.ConcurrentHashMap;

public class Distributed_Rate_Limiter {

    // Token Bucket Class
    static class TokenBucket {
        private int tokens;
        private final int maxTokens;
        private final double refillRate; // tokens per second
        private long lastRefillTime;

        public TokenBucket(int maxTokens, double refillRate) {
            this.maxTokens = maxTokens;
            this.refillRate = refillRate;
            this.tokens = maxTokens;
            this.lastRefillTime = System.currentTimeMillis();
        }

        // Refill tokens based on elapsed time
        private void refill() {
            long now = System.currentTimeMillis();
            double tokensToAdd = (now - lastRefillTime) / 1000.0 * refillRate;

            if (tokensToAdd > 0) {
                tokens = Math.min(maxTokens, tokens + (int) tokensToAdd);
                lastRefillTime = now;
            }
        }

        // Try consuming a token
        public synchronized boolean allowRequest() {
            refill();
            if (tokens > 0) {
                tokens--;
                return true;
            }
            return false;
        }

        public synchronized int getRemainingTokens() {
            refill();
            return tokens;
        }

        public synchronized long getRetryAfterSeconds() {
            if (tokens > 0) return 0;

            double seconds = 1.0 / refillRate;
            return (long) Math.ceil(seconds);
        }
    }

    // Store client buckets
    private final ConcurrentHashMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    // Config: 1000 requests per hour
    private static final int MAX_TOKENS = 1000;
    private static final double REFILL_RATE = 1000.0 / 3600.0; // per second

    // Get or create bucket
    private TokenBucket getBucket(String clientId) {
        return buckets.computeIfAbsent(clientId,
                k -> new TokenBucket(MAX_TOKENS, REFILL_RATE));
    }

    // Check rate limit
    public String checkRateLimit(String clientId) {
        TokenBucket bucket = getBucket(clientId);

        if (bucket.allowRequest()) {
            int remaining = bucket.getRemainingTokens();
            return "Allowed (" + remaining + " requests remaining)";
        } else {
            long retryAfter = bucket.getRetryAfterSeconds();
            return "Denied (0 requests remaining, retry after " + retryAfter + "s)";
        }
    }

    // Get full status
    public String getRateLimitStatus(String clientId) {
        TokenBucket bucket = getBucket(clientId);

        int remaining = bucket.getRemainingTokens();
        int used = MAX_TOKENS - remaining;
        long resetTime = System.currentTimeMillis() + (remaining == 0 ? 3600 * 1000 : 0);

        return "{used: " + used +
                ", limit: " + MAX_TOKENS +
                ", reset: " + (resetTime / 1000) + "}";
    }

    // Main for testing
    public static void main(String[] args) {
        Distributed_Rate_Limiter limiter = new Distributed_Rate_Limiter();

        String clientId = "abc123";

        for (int i = 0; i < 5; i++) {
            System.out.println(limiter.checkRateLimit(clientId));
        }

        System.out.println(limiter.getRateLimitStatus(clientId));
    }
}