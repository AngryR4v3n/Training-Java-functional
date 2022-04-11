import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileReader;
import java.io.IOException;

public class KeyExtractor {

    public JSONObject info;

    public KeyExtractor(String path, String name) throws ParseException, IOException {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader(path+name));
        info = (JSONObject) obj;
    }

    public JSONObject getInfo() {
        return info;
    }
}
