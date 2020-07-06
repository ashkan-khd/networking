import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class CommandParser<T> {
    private String json;
    private Gson gson;

    public CommandParser(Gson gson) {
        this.gson = gson;
    }

    public Command<T> parseToCommand() {
        return gson.fromJson(json, new TypeToken<Command<T>>() {}.getType());
    }

    public T[] parseData() {
        return parseToCommand().getData();
    }

    public T parseDatum() {
        return parseData()[0];
    }

    public void setJson(String json) {
        this.json = json;
    }

}
