package gsix.ATIS.client.systemmanager;

import gsix.ATIS.client.SimpleClient;
import gsix.ATIS.client.common.GuiCommon;
import gsix.ATIS.client.common.MessageEvent;
import gsix.ATIS.client.user.UserHomePageBoundary;
import gsix.ATIS.entities.Community;
import gsix.ATIS.entities.CommunityMessage;
import gsix.ATIS.entities.Message;
import gsix.ATIS.entities.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AddComPageBoundary {
    private Stage stage;
    private User loggedInUser;
    @FXML // fx:id="back_Btn"
    private Button back_Btn;

    @FXML
    private TextField communityNameTF;

    @FXML
    private TextField managerIdTF;

    @FXML // fx:id="addCommunityBtn"
    private Button addCommunityBtn;

    @FXML
    ListView<Community> communityListView;

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert back_Btn != null : "fx:id=\"back_Btn\" was not injected: check your FXML file 'ShowMessageBoxPage.fxml'.";
        assert addCommunityBtn!= null : "fx:id=\"back_Btn\" was not injected: check your FXML file 'ShowMessageBoxPage.fxml'.";
        EventBus.getDefault().register(this);
       //
        getcoms();
    }

    private void getcoms()
    {
        Message message = new Message(1, LocalDateTime.now(), "get all communities", "");
        System.out.println("allcomm");
        try {
            SimpleClient.getClient("",0).sendToServer(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Subscribe
    public void handleTasksEvent(MessageEvent event){
        Message handledMessage = event.getMessage();
        System.out.println("teeeeeeeeeeeest");
        System.out.println(handledMessage.getData());
        // Check if the message type is "get all communities: Done"
        if ("get all communities: Done".equals(handledMessage.getMessage())) {
            // Retrieve and cast the data to List<Community>
            List<Community> data = (List<Community>) handledMessage.getData();
            System.out.println(data);

// Update ListView with received communities
            Platform.runLater(() -> {
                communityListView.getItems().clear(); // Clear existing items
                ObservableList<Community> observableCommunities = FXCollections.observableArrayList(data); // Create ObservableList<Community>
                communityListView.setItems(observableCommunities); // Set the ObservableList to the ListView
            });


    }
        if ("add new community: Done".equals(handledMessage.getMessage())) {
            // Update the ListView or fetch the updated list of communities
            getcoms();

        }
    }



    @FXML
    void sendmsg() {
        String communityName = communityNameTF.getText();
        String managerId = managerIdTF.getText();

        // Create a new Community object
        Community newCommunity = new Community();
        newCommunity.setCommunity_name(communityName);
        newCommunity.setManager_id(managerId);

        // Use the correct message type
        Message message = new Message(1, LocalDateTime.now(), "add new community", newCommunity);

        System.out.println("Sending message to add community");
        try {
            SimpleClient.getClient("", 0).sendToServer(message);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    @FXML
    void goBack(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        GuiCommon guiCommon = GuiCommon.getInstance();
        SystemManagerHomePageBoundary systemManagerHomePageBoundary = (SystemManagerHomePageBoundary) guiCommon
                .displayNextScreen("SystemManagerHomePage.fxml", "System Manager Home Page", null, false);
        systemManagerHomePageBoundary.setLoggedInUser(loggedInUser);
        stage.close();
    }
    public void setLoggedInUser(User user) {

        this.loggedInUser = user;
        String userID = loggedInUser.getUser_id();
    }


}
