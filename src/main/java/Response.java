import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Response<T> {
    private String message;
    private List<T> data;

    @SafeVarargs
    public Response(String message, T... data) {
        this.message = message;
        this.data = Arrays.asList(data);
    }

    public Response() { }

    public String getMessage() {
        return message;
    }

    public List<T> getData() {
        return data;
    }

}
