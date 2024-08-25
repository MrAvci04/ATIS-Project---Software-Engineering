
/**
 * Sample Skeleton for 'ShowMessageBoxPage.fxml' Controller Class
 */

package gsix.ATIS.client.user;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import gsix.ATIS.client.CommunityMessageController;
import gsix.ATIS.client.SimpleClient;
import gsix.ATIS.client.TasksController;
import gsix.ATIS.client.common.GuiCommon;
import gsix.ATIS.client.common.MessageEvent;
import gsix.ATIS.client.common.SosBoundary;
import gsix.ATIS.entities.CommunityMessage;
import gsix.ATIS.entities.Message;
import gsix.ATIS.entities.Task;
import gsix.ATIS.entities.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class ShowMessageBoxController {
    private User loggedInUser=null;
    private Stage stage;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="back_Btn"
    private Button back_Btn; // Value injected by FXMLLoader

    @FXML // fx:id="received_Btn"
    private Button received_Btn; // Value injected by FXMLLoader

    @FXML // fx:id="received_LV"
    private ListView<String> received_LV; // Value injected by FXMLLoader

    @FXML // fx:id="sent_Btn"
    private Button sent_Btn; // Value injected by FXMLLoader

    @FXML // fx:id="sent_LV"
    private ListView<String> sent_LV; // Value injected by FXMLLoader

    //  SOS
    @FXML
    private Button SoS_Btn;
    //  SOS
    //  SOS
    @FXML
    void OpenSosCall(ActionEvent event) {
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow(); // first time stage takes value
        GuiCommon guiCommon = GuiCommon.getInstance();
        SosBoundary sosBoundary = (SosBoundary) guiCommon.displayNextScreen("SosWindow.fxml",
                "SoS Call", stage, false);  // Example for opening new screen
        sosBoundary.setRequester(loggedInUser);
    }
    //  SOS
    /*@FXML
    void ShowReceivedMessages(ActionEvent event) {
        if(loggedInUser!=null) {
            String userId = this.loggedInUser.getUser_id();
            // Initialize the ListView with available tasks
            //getReceivedMessages(userId); //
            CommunityMessageController.getMessageBox(userId);
        }
        else{
            System.out.println("LoggedInUser is null in ShowMessages showReceivedMesages");
        }


    }*/

    private void getReceivedMessages(String userId) {
        Message message = new Message(1, LocalDateTime.now(), "get received messages", userId);
        System.out.println("ShowMessages: Received Messages");
        try {
            SimpleClient.getClient("",0).sendToServer(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void goBack(ActionEvent event) {
        // Implement logic to navigate back to the user's home page
        //open a new page
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow(); // first time stage takes value
        GuiCommon guiCommon = GuiCommon.getInstance();
        UserHomePageBoundary userHomePage = (UserHomePageBoundary) guiCommon.displayNextScreen("UserHomePage.fxml",
                "Community User Home Page", stage, true);  // Example for opening new screen
        userHomePage.setLoggedInUser(loggedInUser);
        EventBus.getDefault().unregister(this);
    }

    /*@FXML
    void showSentMessages(ActionEvent event) {
        if(loggedInUser!=null) {
            String userId = this.loggedInUser.getUser_id();
            // Initialize the ListView with available tasks
            getSentMessages(userId); //
        }
        else{
            System.out.println("LoggedInUser is null in ShowMessages showSentMesages");
        }

    }*/

    private void getSentMessages(String userId) {
        Message message = new Message(1, LocalDateTime.now(), "get sent messages", userId);
        System.out.println("ShowMessages: Sent Messages");
        try {
            SimpleClient.getClient("",0).sendToServer(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert back_Btn != null : "fx:id=\"back_Btn\" was not injected: check your FXML file 'ShowMessageBoxPage.fxml'.";
        assert received_Btn != null : "fx:id=\"received_Btn\" was not injected: check your FXML file 'ShowMessageBoxPage.fxml'.";
        assert received_LV != null : "fx:id=\"received_LV\" was not injected: check your FXML file 'ShowMessageBoxPage.fxml'.";
        assert sent_Btn != null : "fx:id=\"sent_Btn\" was not injected: check your FXML file 'ShowMessageBoxPage.fxml'.";
        assert sent_LV != null : "fx:id=\"sent_LV\" was not injected: check your FXML file 'ShowMessageBoxPage.fxml'.";

        EventBus.getDefault().register(this);
    }
    @Subscribe
    public void handleTasksEvent(MessageEvent event){
        Message handledMessage=event.getMessage();
        //ADDED BY AYAL
        if(handledMessage.getMessage().equals("get sent messages: Done")){
            List<CommunityMessage> sentMessages=(List<CommunityMessage>) handledMessage.getData();
            sentMessages.sort(Comparator.comparing(CommunityMessage::getTime).reversed());
            System.out.println("I am handling the sent messages being brought back from eventbus in ShowMessageBox class");
            List<String> messages_info ;
            //Code to get message sent
            messages_info=getSentMessagesInfo(sentMessages);

            System.out.println(messages_info);
            // Update ListView with received tasks
            Platform.runLater(() -> {
                sent_LV.getItems().clear(); // Clear existing items
                ObservableList<String> observableTasks = FXCollections.observableArrayList(messages_info);
                sent_LV.setItems(observableTasks); // Add received tasks
            });
        }
        if(handledMessage.getMessage().equals("get received messages: Done")){
            List<CommunityMessage> receivedMessages=(List<CommunityMessage>) handledMessage.getData();
            if (receivedMessages != null) {
                receivedMessages.sort(Comparator.comparing(CommunityMessage::getTime).reversed());
                System.out.println("I am handling the received messages being brought back from eventbus in showMessageBox class");
                List<String> messages_info;
                //Code to get message sent
                messages_info = getReceivedMessagesInfo(receivedMessages);
                System.out.println(messages_info);
                // Update ListView with received tasks
                Platform.runLater(() -> {
                    received_LV.getItems().clear(); // Clear existing items
                    ObservableList<String> observableTasks = FXCollections.observableArrayList(messages_info);
                    received_LV.setItems(observableTasks); // Add received tasks
                });
            }
        }
        if(handledMessage.getMessage().equals("Decline Task and Send Decline message to Requester: Done")){
            getReceivedMessages(loggedInUser.getUser_id());
            getSentMessages(loggedInUser.getUser_id());
        }
        if(handledMessage.getMessage().equals("update task status Pending: Done")){
            getReceivedMessages(loggedInUser.getUser_id());
            getSentMessages(loggedInUser.getUser_id());
        }
        if(handledMessage.getMessage().equals("notify InProcess task took so long")) {
            CommunityMessage notification = (CommunityMessage) handledMessage.getData();
            if (notification != null) {
                if (loggedInUser.getUser_id().equals(notification.getSender_id()) || loggedInUser.getUser_id().equals(notification.getReceiver_id())) {
                    getReceivedMessages(loggedInUser.getUser_id());
                    getSentMessages(loggedInUser.getUser_id());
                }
            }
        }
    }
    public static List<String> getSentMessagesInfo(List<CommunityMessage> messages) {
        List<String> messages_info = new ArrayList<>();
        for (CommunityMessage message : messages) {
            messages_info.add("Message ID:" + message.getMessage_id() +  ", Receiver: " + message.getReceiver_id()+ ", Content: " + message.getContent());
        }
        return messages_info;
    }
    public static List<String> getReceivedMessagesInfo(List<CommunityMessage> messages) {
        List<String> messages_info = new ArrayList<>();
        for (CommunityMessage message : messages) {
            messages_info.add("Message ID:" + message.getMessage_id() +  ", Sender: " + message.getSender_id()+ ", Content: " + message.getContent());
        }
        return messages_info;
    }
    public void setLoggedInUser(User loggedInUser) {

        this.loggedInUser=loggedInUser;
        String userID = loggedInUser.getUser_id();
        getReceivedMessages(userID);
        getSentMessages(userID);
    }

}
