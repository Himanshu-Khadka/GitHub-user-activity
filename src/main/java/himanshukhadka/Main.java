package himanshukhadka;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class Main {

    public static void main(String[] args) {
        try {
            Scanner input = new Scanner(System.in);

            // User input for username
            System.out.print("Enter GitHub Username: ");
            String username = input.nextLine();

            // API connection
            URL url = new URL("https://api.github.com/users/" + username + "/events");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int responseCode = connection.getResponseCode();

            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            } else {
                // Read the API response
                StringBuilder line = new StringBuilder();
                Scanner scanner = new Scanner(url.openStream());
                while (scanner.hasNext()) {
                    line.append(scanner.nextLine());
                }
                scanner.close();

                JSONArray jsonArray = new JSONArray(line.toString());

                // Activity counts and details
                Map<String, Integer> activityFrequency = new HashMap<>();
                List<String> activityDetails = new ArrayList<>();

                // Process each activity
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String type = jsonObject.getString("type"); // Type of activity
                    String repo = jsonObject.getJSONObject("repo").getString("name");
                    String createdAt = jsonObject.getString("created_at");

                    // Increment activity frequency
                    activityFrequency.put(type, activityFrequency.getOrDefault(type, 0) + 1);

                    // Generate and store readable activity details
                    String detail = formatActivityDetail(type, repo, createdAt, jsonObject);
                    activityDetails.add(detail);
                }

                //Print Results
                System.out.println("===== GitHub User Activity Summary =====\n");
                System.out.printf("%-20s %-10s\n", "Activity Type", "Frequency");
                System.out.println("-----------------------------------------");
                for (Map.Entry<String, Integer> entry : activityFrequency.entrySet()) {
                    System.out.printf("%-20s %-10d\n", entry.getKey(), entry.getValue());
                }

                System.out.println("\nDetailed Activity Logs:");
                for (String detail : activityDetails) {
                    System.out.println(detail);
                }
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    //Helper method to format activity details
    private static String formatActivityDetail(String type, String repo, String createdAt, JSONObject jsonObject) {
        switch (type) {
            case "PushEvent":
                int commitCount = jsonObject.getJSONObject("payload").getJSONArray("commits").length();
                return "Pushed " + commitCount + " commits to " + repo + " at " + createdAt;
            case "CreateEvent":
                String refType = jsonObject.getJSONObject("payload").getString("ref_type");
                return "Created a " + refType + " in " + repo + " at " + createdAt;
            case "DeleteEvent":
                String ref = jsonObject.getJSONObject("payload").getString("ref");
                return "Deleted " + ref + " in " + repo + " at " + createdAt;
            case "IssuesEvent":
                String action = jsonObject.getJSONObject("payload").getString("action");
                return "Issue " + action + " in " + repo + " at " + createdAt;
            case "PullRequestEvent":
                action = jsonObject.getJSONObject("payload").getString("action");
                return "Pull request " + action + " in " + repo + " at " + createdAt;
            default:
                return type + " occurred in " + repo + " at " + createdAt;
        }
    }
}