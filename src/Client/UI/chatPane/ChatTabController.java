package Client.UI.chatPane;

import Client.UI.TestUI;
import Communication.Message;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import static Client.UI.TestUI.myUser;

public class ChatTabController {
    private String mode, myName = TestUI.myUser.getName();
    private Socket chatSocket;
    private DatagramSocket udpChatSocket;
    private InetAddress address= myUser.getMySocket().getInetAddress();
    private int portOut;

    @FXML
    private javafx.scene.control.TextArea visualizingTextAreaItem, typingTextAreaItem;
    @FXML
    private javafx.scene.control.Button sendButton;

    public void setChatSocket(Socket sock){
        this.chatSocket = sock;
        this.mode = "tcp";
    }

    public void setUdpChatSocket(DatagramSocket udpChatSocket, int portOut) {
        this.udpChatSocket = udpChatSocket;
        this.mode = "udp";
        this.portOut = portOut;
    }

    public void addLine(String username, String content){
        String[] contents = content.split("\n");
        for(String line: contents) {
            visualizingTextAreaItem.appendText("<" + username + "> " + line + "\n");
        }
    }

    public void addLine(String content){
        String[] contents = content.split("\n");
        for(String line: contents) {
            visualizingTextAreaItem.appendText(line + "\n");
        }
    }

    public void typeLine(){
        String tmp;
        if(mode.equals("tcp")) {
            if ((tmp = typingTextAreaItem.getText()) != null && !tmp.equals("") && tmp.getBytes().length < 500) {
                this.addLine(myName, tmp);
                Message sendLine = new Message("OP_FRD_CHT_MSG", tmp);
                sendLine.send(chatSocket);
                typingTextAreaItem.clear();
                typingTextAreaItem.positionCaret(0);
            }
        }else {
            StringBuilder tmpBuilder = new StringBuilder();
            try {
                if ((tmp = typingTextAreaItem.getText()) != null && !tmp.equals("")) {
                    tmpBuilder.append('<');
                    tmpBuilder.append(myName);
                    tmpBuilder.append(">: ");
                    tmpBuilder.append(tmp);
                    String sendString = tmpBuilder.toString();
                    if(sendString.getBytes("UTF-8").length < 512) {
                        DatagramPacket p = new DatagramPacket(
                                sendString.getBytes("UTF-8"), 0,
                                sendString.getBytes("UTF-8").length,
                                address, portOut);
                        udpChatSocket.send(p);
                        typingTextAreaItem.clear();
                        typingTextAreaItem.positionCaret(0);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void keyListener(KeyEvent event){
        if(event.getCode() == KeyCode.ENTER) {
            typeLine();
        }
    }

    public void lockWrite() {
        typingTextAreaItem.setEditable(false);
        typingTextAreaItem.setDisable(true);
        sendButton.setDisable(true);
    }
}
