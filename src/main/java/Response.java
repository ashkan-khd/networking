import java.util.ArrayList;

public class Response<T> {
    private String message;
    private T[] data;

    @SafeVarargs
    public Response(String message, T... data) {
        this.message = message;
        this.data = data;
    }

    public Response() {
    }

    public String getMessage() {
        return message;
    }

    public T[] getData() {
        return data;
    }

}
