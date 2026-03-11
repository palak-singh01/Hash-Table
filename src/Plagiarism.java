import java.util.*;

public class Plagiarism {

    // n-gram -> set of document IDs
    private HashMap<String, Set<String>> ngramIndex;

    // documentId -> list of ngrams
    private HashMap<String, List<String>> documentNgrams;

    private int n = 5; // using 5-grams

    public Plagiarism() {
        ngramIndex = new HashMap<>();
        documentNgrams = new HashMap<>();
    }

    // Break text into n-grams
    private List<String> generateNgrams(String text) {
        String[] words = text.toLowerCase().split("\\s+");
        List<String> ngrams = new ArrayList<>();

        for (int i = 0; i <= words.length - n; i++) {
            StringBuilder sb = new StringBuilder();

            for (int j = 0; j < n; j++) {
                sb.append(words[i + j]).append(" ");
            }

            ngrams.add(sb.toString().trim());
        }

        return ngrams;
    }

    // Add document to database
    public void addDocument(String docId, String text) {

        List<String> ngrams = generateNgrams(text);
        documentNgrams.put(docId, ngrams);

        for (String gram : ngrams) {
            ngramIndex.putIfAbsent(gram, new HashSet<>());
            ngramIndex.get(gram).add(docId);
        }
    }

    // Analyze a document for plagiarism
    public void analyzeDocument(String docId) {

        List<String> ngrams = documentNgrams.get(docId);

        HashMap<String, Integer> matchCount = new HashMap<>();

        for (String gram : ngrams) {

            if (ngramIndex.containsKey(gram)) {

                for (String otherDoc : ngramIndex.get(gram)) {

                    if (!otherDoc.equals(docId)) {
                        matchCount.put(otherDoc,
                                matchCount.getOrDefault(otherDoc, 0) + 1);
                    }
                }
            }
        }

        for (Map.Entry<String, Integer> entry : matchCount.entrySet()) {

            String otherDoc = entry.getKey();
            int matches = entry.getValue();

            double similarity = (matches * 100.0) / ngrams.size();

            System.out.println("Found " + matches +
                    " matching n-grams with \"" + otherDoc + "\"");

            System.out.println("Similarity: " +
                    String.format("%.2f", similarity) + "%");

            if (similarity > 60) {
                System.out.println("PLAGIARISM DETECTED\n");
            } else if (similarity > 10) {
                System.out.println("Suspicious\n");
            }
        }
    }

    // Demo
    public static void main(String[] args) {

        Plagiarism detector = new Plagiarism();

        detector.addDocument("essay_089.txt",
                "Artificial intelligence is transforming the world of technology and research");

        detector.addDocument("essay_092.txt",
                "Artificial intelligence is transforming the world of technology and research in many industries");

        detector.addDocument("essay_123.txt",
                "Artificial intelligence is transforming the world of technology and research rapidly");

        detector.analyzeDocument("essay_123.txt");
    }
}


