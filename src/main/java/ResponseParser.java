import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class ResponseParser {
    private String json;
    private Gson gson;

    public ResponseParser(Gson gson) {
        this.gson = gson;
    }

    public<E, C extends Response> Response<E> parseToResponse(Class<E> type, Class<C> rawType) {
        return gson.fromJson(json, new TypeToken<Response<E>>() {}.getType() );
    }

    public<E, C extends Response> List<E> parseData(Class<E> type, Class<C> rawType) {
        return parseToResponse(type, rawType).getData();
    }

    public<E, C extends Response> E parseDatum(Class<E> type, Class<C> rawType) {
        return parseData(type, rawType).get(0);
    }

    public void setJson(String json) {
        this.json = json;
    }
}
