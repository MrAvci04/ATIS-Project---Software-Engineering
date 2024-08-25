package gsix.ATIS.client.manager;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

import com.mysql.cj.ServerPreparedQuery;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class SendMessageToUser {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="BtnClose"
    private Button BtnClose; // Value injected by FXMLLoader

    @FXML // fx:id="messageTB"
    private TextField messageTB; // Value injected by FXMLLoader

    @FXML // fx:id="BtnSend"
    private Button BtnSend; // Value injected by FXMLLoader
    @FXML // fx:id="taskIdTV"
    private Text taskIdTV; // Value injected by FXMLLoader

    //  SOS
    @FXML
    private Button SoS_Btn;
    //  SOS

    private Stage stage;
    private User loggedInManager;
    private String requesterID;
    RequestedTasks requestedTasks = null;
    private int declinedTaskID;

    public void setRequestedTasks(RequestedTasks requestedTasks) {
        this.requestedTasks = requestedTasks;
    }


    //  SOS
    @FXML
    void OpenSosCall(ActionEvent event) {
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow(); // first time stage takes value
        GuiCommon guiCommon = GuiCommon.getInstance();
        SosBoundary sosBoundary = (SosBoundary) guiCommon.displayNextScreen("SosWindow.fxml",
                "SoS Call", stage, false);  // Example for opening new screen
        sosBoundary.setRequester(loggedInManager);
    }
    //  SOS



    public void setLoggedInManager(User loggedInUser) {
        loggedInManager = loggedInUser;
    }

    public void setRequesterID(String requesterID) {
        this.requesterID = requesterID;
    }
    public void setDeclinedTaskID(int declinedID) {
        this.declinedTaskID = declinedID;
    }

    @FXML
    void Close(ActionEvent event) {
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        /*GuiCommon guiCommon = GuiCommon.getInstance();
        RequestedTasks requestedTasks = (RequestedTasks) guiCommon.displayNextScreen("RequestedTasks.fxml",
                "Manager Home Page", stage, true);  // Example for opening new screen
        requestedTasks.setLoggedInUser(loggedInManager);*/
        stage.close();
        EventBus.getDefault().unregister(this);
    }

    private void showEmptyMessageBoxAlert() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Fill in a message");
        alert.setHeaderText(null);
        alert.setContentText("can't send empty message!");

        // Show the alert dialog
        alert.showAndWait();
    }
    @FXML
    void SendDeclineMsgToUser(ActionEvent event) {
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        String msgTB = messageTB.getText();
        if(msgTB.isEmpty()){
            showEmptyMessageBoxAlert();
            return;
        }
        String msg = "[Declined Task Notification]: Your Requested Task with ID = " + declinedTaskID +
                " is declined.\n" + "Decline Reason: " + msgTB;
        String managerID = loggedInManager.getUser_id();
        //CommunityMessageController.send(managerID,requesterID,msg); //// no requester
        //need code to change status to decline

        // Task requester id is not available here so we put it empty, at server it should been available
        CommunityMessage communityMessage = new CommunityMessage(managerID,"",msg);
        Message declineMessageInfo = new Message(1,declinedTaskID+"",communityMessage);
        // HERE WE ENCAPSULATE DECLINE MESSAGE INTO MESSAGE TYPE AS DATA FIELD
        Message message = new Message(1, LocalDateTime.now(), "Decline Task and Send Decline message to Requester",declineMessageInfo);
        System.out.println("Community Message Controller : sending the message");
        try {
            SimpleClient.getClient("",0).sendToServer(message);

            showDeclineMessageSentAlert();
            stage.close();
            EventBus.getDefault().unregister(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //stage.close();
    }
    @Subscribe
    public void handleTasksEvent(MessageEvent event){
        Message handledMessage=event.getMessage();
        if(handledMessage.getMessage().equals("Decline Task and Send Decline message to Requester: Done")){
       /*     System.out.println("SendToMsgToUser Class in handleTasksEvent");
            //requestedTasks.setDeclineMsgSent(true);
            // Display a pop-up indicating that the message was sent to the user
            //Platform.runLater(() -> showAlert("Message Sent", "The message was successfully sent to the manager."));
            Platform.runLater(() -> {

                showDeclineMessageSentAlert();
                *//*GuiCommon guiCommon = GuiCommon.getInstance();
                RequestedTasks requestedTasks = (RequestedTasks) guiCommon.displayNextScreen("RequestedTasks.fxml",
                        "Manager Home Page", stage, true);  // Example for opening new screen
                requestedTasks.setLoggedInUser(loggedInManager);*//*
                stage.close();
            });




*/
        }

    }

    private void showDeclineMessageSentAlert() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Message Sent");
        alert.setHeaderText(null);
        alert.setContentText("decline message has been sent!");
        // Show the alert dialog
        alert.showAndWait();
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert BtnClose != null : "fx:id=\"back_Btn\" was not injected: check your FXML file 'SendMsgToUser.fxml'.";
        assert messageTB != null : "fx:id=\"message_TF\" was not injected: check your FXML file 'SendMsgToUser.fxml'.";
        assert BtnSend != null : "fx:id=\"send_Btn\" was not injected: check your FXML file 'SendMsgToUser.fxml'.";
        assert taskIdTV != null : "fx:id=\"taskIdTV\" was not injected: check your FXML file 'SendMsgToUser.fxml'.";


        //System.out.println(taskID+" inside send msg to user initialize" );
        EventBus.getDefault().register(this);
    }

}
