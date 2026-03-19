import java.util.*;

public class Autocomplete_System {

    // Trie Node
    static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        Map<String, Integer> frequencyMap = new HashMap<>();
    }

    private TrieNode root;

    public Autocomplete_System() {
        root = new TrieNode();
    }

    // Insert query into Trie
    public void insert(String query, int freq) {
        TrieNode node = root;

        for (char c : query.toCharArray()) {
            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);

            // Update frequency map at each prefix
           node.frequencyMap.put(query,node.frequencyMap.getOrDefault(query,0) + freq);
        }
    }

    // Update frequency (new search)
    public void updateFrequency(String query) {
        insert(query, 1);
    }

    // Get top 10 suggestions for prefix
    public List<String> search(String prefix) {
        TrieNode node = root;

        // Traverse Trie for prefix
        for (char c : prefix.toCharArray()) {
            if (!node.children.containsKey(c)) {
                return new ArrayList<>();
            }
            node = node.children.get(c);
        }

        // Min Heap for Top 10
        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>(Comparator.comparingInt(Map.Entry::getValue));

        for (Map.Entry<String, Integer> entry : node.frequencyMap.entrySet()) {
            pq.offer(entry);
            if (pq.size() > 10) {
                pq.poll(); // remove lowest frequency
            }
        }

        // Extract results (highest first)
        List<String> result = new ArrayList<>();
        while (!pq.isEmpty()) {
            result.add(pq.poll().getKey());
        }

        Collections.reverse(result);
        return result;
    }

    // MAIN METHOD (Testing)
    public static void main(String[] args) {
        Autocomplete_System system = new Autocomplete_System();

        // Insert sample data
        system.insert("java tutorial", 1234567);
        system.insert("javascript", 987654);
        system.insert("java download", 456789);
        system.insert("java 21 features", 1);

        // Search prefix
        List<String> results = system.search("jav");

        System.out.println("Suggestions:");
        int rank = 1;
        for (String s : results) {
            System.out.println(rank++ + ". " + s);
        }

        // Update frequency
        system.updateFrequency("java 21 features");
        system.updateFrequency("java 21 features");

        System.out.println("\nAfter updates:");
        results = system.search("java");
        rank = 1;
        for (String s : results) {
            System.out.println(rank++ + ". " + s);
        }
    }
}