import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class ResponseParser<T> {
    private String json;
    private Gson gson;

    public ResponseParser(Gson gson) {
        this.gson = gson;
    }

    public Response<T> parseToResponse() {
        return gson.fromJson(json, new TypeToken<Response<T>>() {}.getType() );
    }

    public List<T> parseData() {
        return parseToResponse().getData();
    }

    public T parseDatum() {
        return parseData().get(0);
    }

    public void setJson(String json) {
        this.json = json;
    }
}
