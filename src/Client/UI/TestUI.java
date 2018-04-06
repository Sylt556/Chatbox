package Client.UI;

import Client.RMI.UserCallback;
import Client.RMI.UserCallbackImplementation;
import Client.User;
import Server.RMI.CallbackInterface;
import Server.RMI.LoginCallback;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ThreadLocalRandom;

public class TestUI extends Application{

    // the primary Controller for the stage I'm about to create
    public static Controller controller;
    public static Stage pStage;
    // the primary User object, which will be referred to throughout the application
    public static User myUser = new User();
    public static int sessionClientPort, sessionNIOPort;

    @Override
    public void start(Stage primaryStage) throws Exception{
        // pick sessionClientPort at random and then run a recursive function to pick a second random, but different, port
        // sessionClientPort will be used to listen for incoming TCP chat connections
        // sessionNIOPort will be used to listen for incoming TCP NIO file streaming connections
        sessionClientPort = ThreadLocalRandom.current().nextInt(49152, 65535 + 1);
        getRandom();
        // we load the rest of the GUI as a normal fxml component
        FXMLLoader loader = new FXMLLoader(getClass().getResource("clientGUI.fxml"));
        Parent root = (Parent)loader.load();
        // and take care to save its controller in a global variable
        controller = (Controller)loader.getController();
        primaryStage = new Stage();
        primaryStage.setTitle("Social Gossip Client");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        setPrimaryStage(primaryStage);
    }

    private void getRandom(){
        sessionNIOPort = ThreadLocalRandom.current().nextInt(49152, 65535 + 1);
        if(sessionNIOPort == sessionClientPort)
            getRandom();
    }

    public void stop(){
        controller.logoutMenuItem();
    }

    public static Stage getPrimaryStage() {
        return pStage;
    }

    private void setPrimaryStage(Stage pStage) {
        TestUI.pStage = pStage;
    }

}
