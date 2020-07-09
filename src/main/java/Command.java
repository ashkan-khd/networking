import java.util.Arrays;
import java.util.List;

public class Command<T> {
    private String authToken;
    private String message;
    private List<T> data;

    public Command() {
    }

    @SafeVarargs
    public Command(String message, T... data) {
        this.message = message;
        this.data = Arrays.asList(data);
    }

    public Command(String authToken, String message, List<T> data) {
        this.authToken = authToken;
        this.message = message;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public List<T> getData() {
        return data;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
