import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Requestor {

    private KeyExtractor keyGetter;
    private JSONParser jsonParser;

    public Requestor(String pathKey, String file) throws ParseException, IOException {

            keyGetter = new KeyExtractor(pathKey, "keys.json");
            jsonParser = new JSONParser();

    }

    public JSONObject getAirport(String icao) throws IOException, InterruptedException {
        String url = String.format("https://airport-info.p.rapidapi.com/airport?icao=%s", icao);
        System.out.println("Querying...");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("X-RapidAPI-Host", "airport-info.p.rapidapi.com")
                .header("X-RapidAPI-Key", (String) this.keyGetter.getInfo().get("AirportAPI"))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        System.out.println(request.toString());
        HttpResponse<InputStream> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofInputStream());

        try {
            JSONObject convertedResponse = (JSONObject) jsonParser.parse(
                    new InputStreamReader(response.body(), "UTF-8"));
            return convertedResponse;
        } catch (ParseException e) {
            System.out.println("Incomprehensible response: " + response.body());
            return null;
        }
    }

    public JSONObject postAirport(JSONObject airport) throws IOException, InterruptedException {
        String url = "http://localhost:3000/airport/add";
        System.out.println(airport.toJSONString());
        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .header("user-agent", "JavaTerminalApp")
                .uri(URI.create(url))
                .method("POST", HttpRequest.BodyPublishers.ofString(airport.toJSONString()))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        try {
            JSONObject convertedResponse = (JSONObject)jsonParser.parse(response.body());

            return convertedResponse;
        } catch (ParseException e) {
            System.out.println("Incomprehensible response: " + response.body());
            return null;
        } finally {
            System.out.println("\n\n");
            System.out.println("Requestor status: " + response.statusCode());
        }

    }

    public JSONObject closeDb() throws IOException, InterruptedException {
        String url = "http://localhost:3000/airport/close";
        System.out.println("Querying...");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("user-agent", "JavaTerminalApp")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        System.out.println(request.toString());
        HttpResponse<InputStream> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofInputStream());

        try {
            JSONObject convertedResponse = (JSONObject) jsonParser.parse(
                    new InputStreamReader(response.body(), "UTF-8"));
            return convertedResponse;
        } catch (ParseException e) {
            System.out.println("Incomprehensible response: " + response.body());
            return null;
        }
    }

    public String printAirport(JSONObject airport) {

        if (airport.containsKey("error") == false) {
            return "\n\n**Airport found** \n\n" +
                    "IATA: " + airport.get("iata") + "\n" +
                    "Name: " + airport.get("name") + "\n" +
                    "Country: " + airport.get("country") + "\n" +
                    "Contact it at: " +
                    airport.get("phone") + "\n" +
                    "Visit its website at: " +
                    airport.get("website") + "\n";

        } else {
            return "\n\n Error found: " + airport.get("error").toString();
        }
    }
}
