import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Consumer;

public class Server {
    public final static int SERVER_PORT = 8080;
    private ServerSocket serverSocket;
    private static ArrayList<Account> accounts;
    private static ArrayList<Score> scores;
    private static HashMap<String, Account> authTokens;
    private static int counter = 1000;

    private static Object addLock = new Object(), subLock = new Object();

    public Server() throws IOException {
        this.serverSocket = new ServerSocket(SERVER_PORT);
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
            new ClientHandler(clientSocket).start();
        }
    }


    //Login Username Password -> auth
    //say hello -> string
    //auth add 1 2 -> result
    //auth sub 1 2 -> result
    //auth show score

    private static class ClientHandler extends Thread{
        private Socket clientSocket;
        private DataOutputStream outStream;
        private DataInputStream inStream;
        private Controller controller = Controller.getController();
        private Gson gson;
//        private CommandParser<Account> accountCommandParser;
//        private CommandParser<String> stringCommandParser;
//        private CommandParser<Integer> integerCommandParser;
        private CommandParser commandParser;
        private static final Response ERROR_RESPONSE = new Response("Invalid Command!!");
        private static final Response NOT_LOGGED_IN_RESPONSE = new Response("Not Logged In");

        public ClientHandler(Socket clientSocket) throws IOException {
            this.clientSocket = clientSocket;
            inStream = new DataInputStream(new BufferedInputStream(this.clientSocket.getInputStream()));
            outStream = new DataOutputStream(new BufferedOutputStream(this.clientSocket.getOutputStream()));
            gson = new GsonBuilder().setPrettyPrinting().create();
//            accountParser = new CommandParser<>(gson);
//            integerParser = new CommandParser<>(gson);
//            stringParser = new CommandParser<>(gson);
            commandParser = new CommandParser(gson);
        }

        @Override
        public void run() {
            try {
                String output = "";
                String input = inStream.readUTF();
                Class<String> stringClass = String.class;
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode objectNode = objectMapper.readValue(input, ObjectNode.class);
                String message = objectNode.get("message").asText();
                if(message.equals("login")) {
//                    CommandParser<Account> accountCommandParser = new CommandParser<>(gson);
//                    accountCommandParser.setJson(input);
//                    accountParser.setJson(input);
                    commandParser.setJson(input);
                    Command<Account> command = gson.fromJson(input, new TypeToken<Command<Account>>() {}.getType());
                    System.out.println(command.getData().get(0).getUsername());
//                    Account account = accountCommandParser.parseDatum(Account.class);
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
//                    integerParser.setJson(input);
                    commandParser.setJson(input);
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

    }

}
