import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private final static String HOME = "127.0.0.1";
    private Socket mySocket;
    private Scanner in;
    private DataOutputStream outStream;
    private DataInputStream inStream;
    private String authToken;
    private Gson gson;

    public Client() throws IOException {
        this.in = new Scanner(System.in);
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client();
        client.run();
    }

    private void run() throws IOException {
        while (true) {
            String command = in.nextLine();
            String[] splitCommand = command.split("\\s");
            switch (splitCommand[0]) {
                case "login" :
                    login(splitCommand[1], splitCommand[2]);
                    break;
                case "say" :
                    sayHello();
                    break;
                case "add" :
                    addOrSub(true , splitCommand[1], splitCommand[2]);
                    break;
                case "sub" :
                    addOrSub(false, splitCommand[1], splitCommand[2]);
                    break;
                case "show" :
                    showScore();
                    break;
                default:
                    System.out.println("Invalid Command :)");
            }
        }
    }

    private void showScore() throws IOException {
        Command command = new Command("show score");
        Response<Score> scoreResponse = postAndGet(command);
        System.out.println(scoreResponse.getMessage() + "\n" + scoreResponse.getData().get(0));
    }

    private void addOrSub(boolean isAdd,String firstNumber, String secondNumber) throws IOException {
        Command<Integer> addCommand = new Command<>((isAdd ? "add" : "sub"), Integer.parseInt(firstNumber), Integer.parseInt(secondNumber));
        Response<Score> scoreResponse = postAndGet(addCommand);
        System.out.println(scoreResponse.getMessage() + "\n" + scoreResponse.getData().get(0));
    }

    private void sayHello() throws IOException {
        Command command = new Command("say hello");
        Response<String> response = postAndGet(command);
        System.out.println(response.getData().get(0));
    }

    private void login(String username, String password) throws IOException {
        Account account = new Account(username, password);
        Command<Account> loginCommand = new Command<>("login", account);
        System.out.println(loginCommand.getClass());
        Response<String> authResponse = postAndGet(loginCommand);
        System.out.println(authResponse.getMessage());
        authToken = authResponse.getData().get(0);

    }

    private <T, E> Response<T> postAndGet(Command<E> command) throws IOException {
        makeConnection();
        command.setAuthToken(authToken);
        String commandStr = gson.toJson(command,  new TypeToken<Command<E>>(){}.getType());
        System.out.println(command.getData());
        System.out.println(commandStr);
        Command<E> eCommand = gson.fromJson(commandStr, new TypeToken<Command<E>>(){}.getType());
        System.out.println(eCommand.getData());
        outStream.writeUTF(commandStr);
        outStream.flush();
        String responseStr = inStream.readUTF();
        Response<T> response = gson.fromJson(responseStr,  new TypeToken<Response<T>>(){}.getType());
        closeConnection();
        return response;
    }

    private void closeConnection() throws IOException {
        inStream.close();
        outStream.close();
        mySocket.close();
    }

    private void makeConnection() throws IOException {
        mySocket = new Socket(HOME, Server.SERVER_PORT);
        inStream = new DataInputStream(new BufferedInputStream(mySocket.getInputStream()));
        outStream = new DataOutputStream(new BufferedOutputStream(mySocket.getOutputStream()));
    }

}
