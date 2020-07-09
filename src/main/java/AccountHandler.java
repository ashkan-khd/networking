import com.google.gson.reflect.TypeToken;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.function.Consumer;

public class AccountHandler extends Handler {
    private static Object addLock = new Object(), subLock = new Object();
    private static final Response ERROR_RESPONSE = new Response("Invalid Command!!");
    private static final Response NOT_LOGGED_IN_RESPONSE = new Response("Not Logged In");

    public AccountHandler(DataOutputStream outStream, DataInputStream inStream, String message, Server server, String input) {
        super(outStream, inStream, message, server, input);
    }

    /*@Override
    public void run() {
        try {
            String output = "";
            if (message.equals("add") || message.equals("sub")) {
                Command<Integer> command = commandParser.parseToCommand((Class<Integer>)Integer.class, Command.class);
                if(command.getAuthToken() != null && !command.getAuthToken().isEmpty()) {
                    if (message.equals("add"))
                        add(command.getAuthToken(), command.getData().get(0), command.getData().get(1));
                    else
                        sub(command.getAuthToken(), command.getData().get(0), command.getData().get(1));
                    Response<Score> response = new Response<>("Successful", getScoreWithUsername(server.getAccountWithAuthToken(command.getAuthToken())));
                    output = gson.toJson(response);
                } else
                    output = gson.toJson(NOT_LOGGED_IN_RESPONSE);
            } else if(message.equals("show score")) {
                Command command = commandParser.parseToCommand((Class<Object>)Object.class, Command.class);
                if(command.getAuthToken() != null && !command.getAuthToken().isEmpty()) {
                    Response<Score> scoreResponse = new Response<>("Your Score : ", getScoreWithUsername(server.getAccountWithAuthToken(command.getAuthToken())));
                    output = gson.toJson(scoreResponse);
                } else
                    output = gson.toJson(NOT_LOGGED_IN_RESPONSE);
            } else {
                output = gson.toJson(ERROR_RESPONSE);
            }
            outStream.writeUTF(output);
            outStream.flush();
            System.out.println(new Date());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    @Override
    protected String handle() {
        String output = "";
        if (message.equals("add") || message.equals("sub")) {
            Command<Integer> command = commandParser.parseToCommand((Class<Integer>)Integer.class, Command.class);
            if(command.getAuthToken() != null && !command.getAuthToken().isEmpty()) {
                if (message.equals("add"))
                    add(command.getAuthToken(), command.getData().get(0), command.getData().get(1));
                else
                    sub(command.getAuthToken(), command.getData().get(0), command.getData().get(1));
                Response<Score> response = new Response<>("Successful", getScoreWithUsername(server.getAccountWithAuthToken(command.getAuthToken())));
                output = gson.toJson(response);
            } else
                output = gson.toJson(NOT_LOGGED_IN_RESPONSE);
        } else if(message.equals("show score")) {
            Command command = commandParser.parseToCommand((Class<Object>)Object.class, Command.class);
            if(command.getAuthToken() != null && !command.getAuthToken().isEmpty()) {
                Response<Score> scoreResponse = new Response<>("Your Score : ", getScoreWithUsername(server.getAccountWithAuthToken(command.getAuthToken())));
                output = gson.toJson(scoreResponse);
            } else
                output = gson.toJson(NOT_LOGGED_IN_RESPONSE);
        } else {
            output = gson.toJson(ERROR_RESPONSE);
        }
        return output;
    }

    private void add(String auth, int a, int b) {
        server.doMath(auth, a, b, score -> {
            try {
                score.addNumber(controller.add(a, b, addLock));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private void sub(String auth, int a, int b) {
        server.doMath(auth, a, b, score -> {
            try {
                score.addNumber(controller.sub(a, b, subLock));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private Score getScoreWithUsername(Account account) {
        for (Score score : server.getScores()) {
            if(score.equals(account))
                return score;
        }
        return null;
    }
}
