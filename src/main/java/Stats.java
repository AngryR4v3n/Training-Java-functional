import org.json.simple.JSONObject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class Stats{
    private JSONObject info;

    public Stats(JSONObject info){
        this.info = info;
    }

    public Map<Object, Long> getUniqueCountriesCount(){
        Object [] countries = getCountryList();
        //count per country
        Map<Object, Long> countForId = Arrays.
                stream(countries)
                .parallel()
                .collect(groupingBy(Function.identity(), counting()));

        return countForId;
    }

    public Map<Object, Double> getPercentagePerCountry(){
        Map<Object, Long> counts = getUniqueCountriesCount();
        Object[] countries = getCountryUniqueList();
        Map<Object, Double> percentages = new HashMap<>();

        for (Object country: countries) {
            Double count = (double) (counts.get(country));
            percentages.put(country, ((count/this.info.size())*100));
        }
        return percentages;
    }

    public Object [] getCountryUniqueList(){
        return this.info.values()
                .stream()
                .map(getCountries).distinct().toArray();
    }

    public Object [] getCountryList(){
        return this.info.values()
                .stream()
                .map(getCountries).toArray();
    }

    public Map<String, String> getCoordinatesByCountry(String country){

        return (Map) this.info.values()
                .stream()
                .filter(val -> filterCountry.test((JSONObject) val, country))
                .collect(Collectors.toMap((JSONObject val) -> val.get("icao"), (JSONObject val) -> val.get("lat").toString() + " , " + val.get("lon").toString()));
    }

    // average elevation
    public Double getAverageElevation(){
        return this.info.values()
                .stream()
                .mapToLong(getElevation).average().orElse(Double.NaN);
    }

    public Double getAverageElevation(String country){
        return this.info.values()
                .stream()
                .filter(val -> filterCountry.test((JSONObject) val, country))
                .mapToLong(getElevation).average().orElse(Double.NaN);
    }

    public Map<String, String> getAirportByCountry(String country){
        return (Map<String, String>) this.info.values()
                .stream()
                .filter(val -> filterCountry.test((JSONObject) val, country))
                .collect(Collectors.toMap((JSONObject val) -> val.get("icao"), (JSONObject val) -> val.get("name")));
    }

    public Map<String, JSONObject> getAirportByName(String name){
        return (Map<String, JSONObject>) this.info.values()
                .stream()
                .filter(val -> getByName.test((JSONObject) val, name))
                .collect(Collectors.toMap((JSONObject val) -> val.get("icao"), (JSONObject val) -> val));
    }

    public JSONObject getAirport(String icao){
        return (JSONObject) this.info.get(icao);
    }

    private BiPredicate<JSONObject, String> getByName = (value, name) -> {

        return value.get("name").toString().contains(name);
    };

    private ToLongFunction<JSONObject> getElevation = value -> {
        return (long) value.get("elevation");
    };

    private Function<JSONObject, String> getCountries = value -> {
        return (String) value.get("country");
    };

    private Function<JSONObject, String> getName = value -> {
        return value.get("icao").toString() + ":    " + value.get("name").toString();
    };

    private BiPredicate<JSONObject, String> filterCountry = (value, country) -> {
      return value.get("country").equals(country);
    };
}