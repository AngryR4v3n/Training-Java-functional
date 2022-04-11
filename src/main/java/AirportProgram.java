import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public class AirportProgram {

    public static void main(String[] args) throws ParseException, IOException {
        Scanner reader = new Scanner(System.in);
        final String ANSI_RED_BACKGROUND = "\u001B[41m";
        final String ANSI_RESET = "\u001B[0m";
        final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
        System.out.println("===================\n");
        System.out.println("Welcome to WikiAir");
        System.out.println("\n===================");
        System.out.print("Please enter the ABSOLUTE PATH where the key.json and airports.json are located (they must be in the same folder): ");
        String path = reader.nextLine();

        try {
            Requestor myRequestor = new Requestor(path, "keys.json");
            System.out.println("Getting IATA codes");
            KeyExtractor extractor = new KeyExtractor(path, "airports.json");
            Stats stats = new Stats(extractor.getInfo());

            while(true){
                //menu choice...
                System.out.println("\n\n\n");
                System.out.println("===================\n");
                System.out.println("Welcome to WikiAir");
                System.out.println("\n===================");
                System.out.println("===============================================");
                System.out.println("1. Print all the registered countries");
                System.out.println("2. Get airports coordinates by country");
                System.out.println("3. Get airport names by country");
                System.out.println("4. Get average airport elevation by country");
                System.out.println("5. Get average airport elevation");
                System.out.println("6. Get count by country");
                System.out.println("7. Get percentage by country");
                System.out.println("8. Get airport information ");
                System.out.println("9. Get airport by name");
                System.out.println("\n**EXTERNAL API USAGE**\n");
                System.out.println("10. Get airport info");
                System.out.println("11. Post airport info");
                System.out.println("12. Close external DB");
                System.out.println("\n");
                System.out.println(ANSI_RED_BACKGROUND+"13. Exit program" + ANSI_RESET);
                System.out.println("===============================================");
                System.out.print("Choose an option: ");
                String choice = reader.nextLine();

                switch (choice){
                    case "1":
                        Object[] x = stats.getCountryList();
                        System.out.println("Printing countries");
                        for (Object country: x){
                            System.out.println(country);
                        }
                        System.out.println("\nTotal registered countries: " + x.length);
                        x = null;
                        System.gc();
                        break;

                    case "2":
                        System.out.println("\n\n");
                        System.out.print("Please type the country you want to query: ");
                        String country = reader.nextLine();
                        Map<String, String> myCoord =stats.getCoordinatesByCountry(country);
                        myCoord.forEach((k, v) -> System.out.println("ICAO: " + k + "   Coordinates: " + v));

                        System.out.println("\nTotal airports: " + myCoord.size());
                        x = null;
                        System.gc();
                        break;

                    case "3":
                        System.out.println("\n\n");
                        System.out.print("Please type the country you want to query: ");
                        country = reader.nextLine();
                        Map<String, String> airports = stats.getAirportByCountry(country);
                        airports.forEach((key, value) -> System.out.println(key + ": " + value));
                        System.out.println("\nTotal airports: " + airports.size());
                        break;

                    case "4":
                        System.out.println("\n\n");
                        System.out.print("Please type the country you want to query: ");
                        country = reader.nextLine();
                        Double elevation = stats.getAverageElevation(country);
                        System.out.println("Avg. elevation: " + elevation);
                        break;

                    case "5":
                        System.out.println("\n\n");
                        elevation = stats.getAverageElevation();
                        System.out.println("Avg. elevation: " + elevation);
                        break;

                    case "6":
                        System.out.println("\n\n");
                        Map<Object, Long> counts = stats.getUniqueCountriesCount();
                        counts.forEach((key, value)-> System.out.println(key + ": " + value.toString()));
                        break;

                    case "7":
                        System.out.println("\n\n");
                        Map<Object, Double> percentages = stats.getPercentagePerCountry();
                        percentages.forEach((key, value)-> System.out.println(key + ": " + String.format("%.4f",value) + "%"));
                        break;

                    case "8":
                        System.out.println("\n\n");
                        System.out.print("Please type the ICAO airport code you want to query: ");
                        String icao = reader.nextLine();
                        JSONObject res = stats.getAirport(icao);
                        System.out.println(res.toJSONString());
                        break;

                    case "9":
                        System.out.println("\n\n");
                        System.out.print("Please type the airport name you want to query: ");
                        String name = reader.nextLine();
                        Map<String, JSONObject> airInfo = stats.getAirportByName(name);
                        System.out.println();
                        airInfo.values().forEach((JSONObject v) -> {
                            System.out.println("-------------------------------------------");
                            System.out.println();
                            System.out.println("Name: " + v.get("name"));
                            System.out.println("ICAO: " + v.get("icao"));
                            System.out.println("Country: " + v.get("country"));
                            System.out.println("State: " + v.get("state"));
                            System.out.println("Elevation: " + v.get("elevation"));
                            System.out.println("lon: " + v.get("lon"));
                            System.out.println("lat: " + v.get("lat"));
                            System.out.println("Time-zone: " + v.get("tz"));
                            System.out.println();
                            System.out.println("-------------------------------------------");
                        });
                        break;

                    case "10":
                        System.out.print("Please type the ICAO airport code you want to query: ");
                        String iata = reader.nextLine();
                        JSONObject response = myRequestor.getAirport(iata);
                        String air = myRequestor.printAirport(response);
                        System.out.println(air);
                        break;

                    case "11":
                        System.out.print("Please type the ICAO airport code you want to POST: ");
                        iata = reader.nextLine();
                        response = myRequestor.getAirport(iata);
                        air = myRequestor.printAirport(response);
                        System.out.println("****POSTING****");
                        System.out.println(air);
                        myRequestor.postAirport(response);
                        break;

                    case "12":
                        System.out.println("****CLOSING DB****");
                        response = myRequestor.closeDb();
                        System.out.println(response);
                        break;

                    case "13":
                        System.out.println("\n\n");
                        System.out.println(ANSI_GREEN_BACKGROUND+"Bye, thanks for using Wiki Air "+ANSI_RESET);
                        System.out.println(ANSI_GREEN_BACKGROUND+"Program written by Fran. Github: molinajimenez"+ANSI_RESET);
                        System.out.println("Airports json obtained from: https://raw.githubusercontent.com/mwgg/Airports/master/airports.json ");
                        System.exit(0);

                    default:
                        System.out.println("Undefined choice");
                        break;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        } catch (ParseException e) {
            System.out.println("Parsing error, try downloading the file again or fixing it!");
        } catch (IOException e) {
            System.out.println("IO, probably file deleted?");
        } catch (InterruptedException e) {
        }

    }
}
