package Communication;

import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Message {

    private String operation;
    private String data;
    private StringBuilder incomingJson;
    private String toParse;
    private JSONParser parser;
    private JSONObject jsonObject = new JSONObject();
    //BufferedReader reader = null;
    //BufferedWriter writer = null;

    // if the getters return null the body knows no message was received
    public String getOperation() {
        return operation;
    }

    public String getData() {
        return data;
    }

    // Constructor called when sending a message
    @SuppressWarnings("unchecked")
    public Message(String operation, String data) {
        this.operation = operation;
        jsonObject.put("OP_CODE", operation);
        this.data = data;
        jsonObject.put("DATA", data);
    }

    // Constructor called when receiving one, only instances the object
    public Message(){
    }

    // Call this method on a populated Message Object
    public void send (Socket server){
        if(operation != null && data != null)
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
            writer.write(jsonObject.toJSONString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        else System.out.println("Missing parameters in the send method on the Message object, you might be trying to send a message built for receiving or with a missing INetAddress.");
    }

    private void parseIncomingJson(){
        parser = new JSONParser();
        try {
            jsonObject = (JSONObject) parser.parse(toParse);
            this.operation = (String) jsonObject.get("OP_CODE");
            this.data = (String) jsonObject.get("DATA");
        }catch (Exception e) {e.printStackTrace();}
    }

    // Call the receive method on an empty Message object
    // this method first receives, then parses a new JSON
    public void receive(Socket server){
        if(!server.isClosed()) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(server.getInputStream()));
                toParse = reader.readLine();
                reader.close();
                this.parseIncomingJson();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
