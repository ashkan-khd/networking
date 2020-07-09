import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class Server {
    public final static int SERVER_PORT = 8080;
    private ServerSocket serverSocket;
    private ObjectMapper mapper;
    private ArrayList<Account> accounts;
    private ArrayList<Score> scores;
    private HashMap<String, Account> authTokens;
    private int counter = 1000;

    private static Object addLock = new Object(), subLock = new Object();

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

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.run();
    }

    private void run() throws IOException {
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
                    new GeneralHandler(outStream, inStream, message, this).start();
                    break;
                case "add":
                case "sub":
                case "show score":

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

    public ArrayList<Score> getScores() {
        return scores;
    }

    public HashMap<String, Account> getAuthTokens() {
        return authTokens;
    }

    //Login Username Password -> auth
    //say hello -> string
    //auth add 1 2 -> result
    //auth sub 1 2 -> result
    //auth show score

/*    private static class ClientHandler extends Thread{
        private Socket clientSocket;
        private DataOutputStream outStream;
        private DataInputStream inStream;
        private Controller controller = Controller.getController();
        private Gson gson;
        private ObjectMapper mapper;
        private CommandParser commandParser;
        private static final Response ERROR_RESPONSE = new Response("Invalid Command!!");
        private static final Response NOT_LOGGED_IN_RESPONSE = new Response("Not Logged In");

        public ClientHandler(Socket clientSocket) throws IOException {
            this.clientSocket = clientSocket;
            inStream = new DataInputStream(new BufferedInputStream(this.clientSocket.getInputStream()));
            outStream = new DataOutputStream(new BufferedOutputStream(this.clientSocket.getOutputStream()));
            gson = new GsonBuilder().setPrettyPrinting().create();
            mapper = new ObjectMapper();
            commandParser = new CommandParser(gson);
        }

        @Override
        public void run() {
            try {
                String output = "";
                String input = inStream.readUTF();
                commandParser.setJson(input);
                ObjectNode objectNode = mapper.readValue(input, ObjectNode.class);
                String message = objectNode.get("message").asText();
                if(message.equals("login")) {
                    Account account = commandParser.parseDatum((Class<Account>)Account.class, Command.class);
                    if (accounts.contains(account) && accounts.get(accounts.indexOf(account)).getPassword().equals(account.getPassword())) {
                        Response<String> response = new Response<>("Logged In", "" + (++counter));
                        output = gson.toJson(response);
                        authTokens.put("" + counter, account);
                    } else {
                        Response response = new Response("Unsuccessful");
                        output = gson.toJson(response);
                    }
                } else if (message.equals("say hello")) {
                    Response<String> response = new Response<>("Kir tot", controller.getHello());
                    output = gson.toJson(response, new TypeToken<Response<String>>() {}.getType());
                } else if (message.equals("add") || message.equals("sub")) {
                    Command<Integer> command = commandParser.parseToCommand((Class<Integer>)Integer.class, Command.class);
                    if(command.getAuthToken() != null && !command.getAuthToken().isEmpty()) {
                        if (message.equals("add"))
                            add(command.getAuthToken(), command.getData().get(0), command.getData().get(1));
                        else
                            sub(command.getAuthToken(), command.getData().get(0), command.getData().get(1));
                        Response<Score> response = new Response<>("Successful", getScoreWithUsername(authTokens.get(command.getAuthToken())));
                        output = gson.toJson(response);
                    } else
                        output = gson.toJson(NOT_LOGGED_IN_RESPONSE);
                } else if(message.equals("show score")) {
                    Command command = commandParser.parseToCommand((Class<Object>)Object.class, Command.class);
                    if(command.getAuthToken() != null && !command.getAuthToken().isEmpty()) {
                        Response<Score> scoreResponse = new Response<>("Your Score : ", getScoreWithUsername(authTokens.get(command.getAuthToken())));
                        output = gson.toJson(scoreResponse);
                    } else
                        output = gson.toJson(NOT_LOGGED_IN_RESPONSE);
                } else {
                    output = gson.toJson(ERROR_RESPONSE);
                }
                System.out.println("Date: " + new Date());
                outStream.writeUTF(output);
                outStream.flush();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void add(String auth, int a, int b) {
            doMath(auth, a, b, score -> {
                try {
                    score.addNumber(controller.add(a, b, addLock));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        private void sub(String auth, int a, int b) {
            doMath(auth, a, b, score -> {
                try {
                    score.addNumber(controller.sub(a, b, subLock));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        private Score getScoreWithUsername(Account account) {
            for (Score score : scores) {
                if(score.equals(account))
                    return score;
            }
            return null;
        }

        private void doMath(String auth, int a, int b, Consumer<Score> scoreConsumer) {
            scores.stream().filter(score -> score.equals(authTokens.get(auth))).forEach(scoreConsumer);
        }

    }*/

}
