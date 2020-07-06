import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ResponseParser<T> {
    private String json;
    private Gson gson;

    public ResponseParser(Gson gson) {
        this.gson = gson;
    }

    public Response<T> parseToResponse() {
        return gson.fromJson(json, new TypeToken<Response<T>>() {}.getType() );
    }

    public T[] parseData() {
        return parseToResponse().getData();
    }

    public T parseDatum() {
        return parseData()[0];
    }

    public void setJson(String json) {
        this.json = json;
    }
}
