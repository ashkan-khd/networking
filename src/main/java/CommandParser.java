import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class CommandParser<T> {
    private String json;
    private Gson gson;

    public CommandParser(Gson gson) {
        this.gson = gson;
    }

    public<E, C extends Command> Command<E> parseToCommand(Class<E> type, Class<C> rawType) {
        return gson.fromJson(json, /*new TypeToken<Command<E>>() {}.getType()*/TypeToken.getParameterized(rawType, type).getType());
    }

    public<E, C extends Command> List<E> parseData(Class<E> type, Class<C> rawType) {
        return parseToCommand(type, rawType).getData();
    }

    public<E, C extends Command> E parseDatum(Class<E> type, Class<C> rawType) {
        return parseData(type, rawType).get(0);
    }

    public void setJson(String json) {
        this.json = json;
    }

}
