public class Command<T> {
    private String authToken;
    private String message;
    private T[] data;

    public Command() {
    }

    @SafeVarargs
    public Command(String message, T... data) {
        this.message = message;
        this.data = data;
    }

    public Command(String authToken, String message, T[] data) {
        this.authToken = authToken;
        this.message = message;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public T[] getData() {
        return data;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
