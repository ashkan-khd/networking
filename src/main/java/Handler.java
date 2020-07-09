import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

public abstract class Handler extends Thread{
    protected DataOutputStream outStream;
    protected DataInputStream inStream;
    protected Controller controller;
    protected Gson gson;
    protected CommandParser commandParser;
    protected String message;
    protected Server server;

    public Handler(DataOutputStream outStream, DataInputStream inStream, String message, Server server, String input) {
        this.outStream = outStream;
        this.inStream = inStream;
        this.controller = Controller.getController();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.commandParser = new CommandParser(gson);
        commandParser.setJson(input);
        this.message = message;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            String output = handle();
            outStream.writeUTF(output);
            outStream.flush();
            System.out.println(new Date());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    //Login Username Password -> auth
    //say hello -> string
    //auth add 1 2 -> result
    //auth sub 1 2 -> result
    //auth show score
    abstract protected String handle() throws InterruptedException;

}
