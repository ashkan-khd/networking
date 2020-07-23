import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

public class Server {
    public final static int SERVER_PORT = 8080;
    private ServerSocket serverSocket;
    private ObjectMapper mapper;
    private ArrayList<Account> accounts;
    private ArrayList<Score> scores;
    private HashMap<String, Account> authTokens;
    private int counter = 1000;

    public Server() throws IOException {
        this.serverSocket = new ServerSocket(SERVER_PORT);
        mapper = new ObjectMapper();
        accounts = new ArrayList<>();
        authTokens = new HashMap<>();
        scores = new ArrayList<>();
        Account ashkan = new Account("Ashkan", "1234");
        Account hossein = new Account("Hossein", "1234");
        accounts.add(ashkan);
        accounts.add(hossein);
        scores.add(new Score(ashkan, 0));
        scores.add(new Score(hossein, 0));
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = new Server();
        server.run();
    }

    private void run() throws IOException, InterruptedException {
        while (true) {
            System.out.println("Server Listening...");
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client Accepted");
            DataInputStream inStream = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
            DataOutputStream outStream = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
            String input = inStream.readUTF();
            ObjectNode objectNode = mapper.readValue(input, ObjectNode.class);
            String message = objectNode.get("message").asText();

            switch (message) {
                case "say hello" :
                case "login" :
                    new GeneralHandler(outStream, inStream, message, this, input).start();
                    break;
                case "add":
                case "sub":
                case "show score":
                    new AccountHandler(outStream, inStream, message, this, input).start();
                    break;
                case "get picture":
                    System.out.println("0");
                    new PictureHandler(outStream, inStream, message, this, input).start();
                    break;
                default:
                    outStream.writeUTF("Invalid Command");
                    outStream.flush();
            }
        }
    }

    public ArrayList<Account> getAccounts() {
        return accounts;
    }

    public void addCounter() {
        counter++;
    }

    public int getCounter() {
        return counter;
    }

    public void addAuth(Account account) {
        authTokens.put("" + counter, account);
    }

    public Account getAccountWithAuthToken(String authToken) {
        if(authTokens.containsKey(authToken))
            return authTokens.get(authToken);

        return null;
    }

    public ArrayList<Score> getScores() {
        return scores;
    }


    public void doMath(String auth, Consumer<Score> scoreConsumer) {
        scores.stream().filter(score -> score.equals(authTokens.get(auth))).forEach(scoreConsumer);
    }

}
