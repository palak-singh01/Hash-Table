import java.util.*;

public class SocialMedia {

    // username -> userId
    private HashMap<String, Integer> usernameMap;

    // username -> number of attempts
    private HashMap<String, Integer> attemptFrequency;

    public SocialMedia() {
        usernameMap = new HashMap<>();
        attemptFrequency = new HashMap<>();
    }

    // Register a new user
    public void registerUser(String username, int userId) {
        usernameMap.put(username, userId);
    }

    // Check username availability (O(1))
    public boolean checkAvailability(String username) {

        // Track attempts
        attemptFrequency.put(username,
                attemptFrequency.getOrDefault(username, 0) + 1);

        return !usernameMap.containsKey(username);
    }

    // Suggest alternative usernames
    public List<String> suggestAlternatives(String username) {

        List<String> suggestions = new ArrayList<>();

        // Append numbers
        for (int i = 1; i <= 5; i++) {
            String suggestion = username + i;
            if (!usernameMap.containsKey(suggestion)) {
                suggestions.add(suggestion);
            }
        }

        // Replace underscore with dot
        if (username.contains("_")) {
            String dotVersion = username.replace("_", ".");
            if (!usernameMap.containsKey(dotVersion)) {
                suggestions.add(dotVersion);
            }
        }

        // Add random number suggestion
        String randomSuggestion = username + new Random().nextInt(1000);
        if (!usernameMap.containsKey(randomSuggestion)) {
            suggestions.add(randomSuggestion);
        }

        return suggestions;
    }

    // Get the most attempted username
    public String getMostAttempted() {

        String mostAttempted = null;
        int max = 0;

        for (Map.Entry<String, Integer> entry : attemptFrequency.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                mostAttempted = entry.getKey();
            }
        }

        return mostAttempted + " (" + max + " attempts)";
    }

    // Demo
    public static void main(String[] args) {

        SocialMedia sm = new SocialMedia();

        sm.registerUser("john_doe", 101);
        sm.registerUser("admin", 102);

        System.out.println(sm.checkAvailability("john_doe"));   // false
        System.out.println(sm.checkAvailability("jane_smith")); // true

        System.out.println(sm.suggestAlternatives("john_doe"));

        sm.checkAvailability("admin");
        sm.checkAvailability("admin");
        sm.checkAvailability("admin");

        System.out.println(sm.getMostAttempted());
    }
}