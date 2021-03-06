import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.BufferOverflowException;
import java.util.ArrayList;
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
                case "get" :
                    getImage();
                    break;
                default:
                    System.out.println("Invalid Command :)");
            }
        }
    }

    private void getImage() throws IOException {
        Command command = new Command("get picture");
        System.out.println("1");
        makeConnection();
        System.out.println("2");
        outStream.writeUTF(gson.toJson(command));
        System.out.println("3");
        outStream.flush();
        System.out.println("4");
        ArrayList<Integer> integers = new ArrayList<>();
        int i;
        while ((i = inStream.read()) > -1) {
            integers.add(i);
            System.out.println("i : " + i);
        }
        System.out.println("Integers Size : " + integers.size());
        System.out.println("5");
        byte[] bytes = new byte[integers.size()];
        System.out.println("6");
        for (int j = 0; j < integers.size(); j++) {
            bytes[j] = integers.get(j).byteValue();
            System.out.println("J : " + j);
        }
        System.out.println("Bytes Size : " + bytes.length);
        System.out.println("7");
        InputStream inputStream = new ByteArrayInputStream(bytes);
        System.out.println(inputStream);
        BufferedImage bufferedImage = ImageIO.read(inputStream);
        System.out.println("Buffered Image : " + bufferedImage);
        System.out.println("8");
        System.out.println("Width : " + bufferedImage.getWidth());
        System.out.println("Height : " + bufferedImage.getHeight());
        closeConnection();
    }

    private void showScore() throws IOException {
        Command command = new Command("show score");
        Response<Score> scoreResponse = postAndGet(command, (Class<Score>)Score.class, Response.class);
        System.out.println(scoreResponse.getMessage() + "\n" + scoreResponse.getData().get(0));
    }

    private void addOrSub(boolean isAdd,String firstNumber, String secondNumber) throws IOException {
        Command<Integer> addCommand = new Command<>((isAdd ? "add" : "sub"), Integer.parseInt(firstNumber), Integer.parseInt(secondNumber));
        Response<Score> scoreResponse = postAndGet(addCommand, (Class<Score>)Score.class, Response.class);
        System.out.println(scoreResponse.getMessage() + "\n" + scoreResponse.getData().get(0).toString());
    }

    private void sayHello() throws IOException {
        Command command = new Command("say hello");
        Response<String> response = postAndGet(command, (Class<String>)String.class, Response.class);
        System.out.println(response.getData().get(0));
    }

    private void login(String username, String password) throws IOException {
        Account account = new Account(username, password);
        Command<Account> loginCommand = new Command<>("login", account);
        Response<String> authResponse = postAndGet(loginCommand, (Class<String>)String.class, Response.class);
        System.out.println(authResponse.getMessage());
        authToken = authResponse.getData().get(0);

    }

    private <T, E, C extends Response> Response<T> postAndGet(Command<E> command, Class<T> responseDataType, Class<C> responseType) throws IOException {
        makeConnection();
        command.setAuthToken(authToken);
        String commandStr = gson.toJson(command,  new TypeToken<Command<E>>(){}.getType());
        System.out.println(commandStr);
        outStream.writeUTF(commandStr);
        outStream.flush();
        String responseStr = inStream.readUTF();
        Response<T> response = gson.fromJson(responseStr,  TypeToken.getParameterized(responseType, responseDataType).getType());
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
