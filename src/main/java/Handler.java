import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public abstract class Handler extends Thread{
    protected DataOutputStream outStream;
    protected DataInputStream inStream;
    protected Controller controller;
    protected Gson gson;
    protected CommandParser commandParser;
    protected String message;
    protected Server server;

    public Handler(DataOutputStream outStream, DataInputStream inStream, String message, Server server) {
        this.outStream = outStream;
        this.inStream = inStream;
        this.controller = Controller.getController();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.commandParser = new CommandParser(gson);
        this.message = message;
        this.server = server;
    }

}
