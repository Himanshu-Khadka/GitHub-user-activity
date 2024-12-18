package himanshukhadka;


import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;


public class Main{

    List<String> actions = new ArrayList<>();



    public static void main(String[] args){
        try {
            URL url = new URL("https://api.github.com/users/Himanshu-Khadka/events");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int respondCode = connection.getResponseCode();

            if(respondCode != 200){
                throw new RuntimeException("HttpResponseCode: " + respondCode);
            }else{
                String line = "";
                Scanner scanner = new Scanner(url.openStream());
                while (scanner.hasNext()) {
                    line += scanner.nextLine();
                }
                scanner.close();
                System.out.println(line);

                JSONArray jsonArray = new JSONArray(line);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String type = jsonObject.getString("type");
                    String repo = jsonObject.getJSONObject("repo").getString("name");
                    String created_at = jsonObject.getString("created_at");
                    System.out.println("Type: " + type + " Repo: " + repo + " Created At: " + created_at);
                }
            }
        }catch (Exception e){
            System.out.println("Error: " + e);
        }

    }
}


