import com.google.gson.reflect.TypeToken;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class GeneralHandler extends Handler {


    public GeneralHandler(DataOutputStream outStream, DataInputStream inStream, String message, Server server) {
        super(outStream, inStream, message, server);
    }

    @Override
    public void run() {
        try {
            String output = "";
            if(message.equals("login")) {
                Account account = commandParser.parseDatum((Class<Account>)Account.class, Command.class);
                if (server.getAccounts().contains(account) && server.getAccounts().get(server.getAccounts().indexOf(account)).getPassword().equals(account.getPassword())) {
                    server.addCounter();
                    Response<String> response = new Response<>("Logged In", "" + (server.getCounter()));
                    output = gson.toJson(response);
                    server.addAuth(account);
                } else {
                    Response response = new Response("Unsuccessful");
                    output = gson.toJson(response);
                }
            } else if (message.equals("say hello")) {
                Response<String> response = new Response<>("Kir tot", controller.getHello());
                output = gson.toJson(response, new TypeToken<Response<String>>() {}.getType());
            }
            outStream.writeUTF(output);
            outStream.flush();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }

    }
}
