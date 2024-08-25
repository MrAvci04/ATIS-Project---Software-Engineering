/**
 * Sample Skeleton for 'CommunityMembers.fxml' Controller Class
 */

package gsix.ATIS.client.manager;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import gsix.ATIS.client.SimpleClient;
import gsix.ATIS.client.TasksController;
import gsix.ATIS.client.common.GuiCommon;
import gsix.ATIS.client.common.MessageEvent;
import gsix.ATIS.client.common.SosBoundary;
import gsix.ATIS.entities.Message;
import gsix.ATIS.entities.Task;
import gsix.ATIS.entities.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class CommunityMembers {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="btnBack"
    private Button btnBack; // Value injected by FXMLLoader

    @FXML // fx:id="btnRequests"
    private Button btnRequests; // Value injected by FXMLLoader

    @FXML // fx:id="btnVolunteered"
    private Button btnVolunteered; // Value injected by FXMLLoader

    @FXML // fx:id="membersLV"
    private ListView<String> membersLV; // Value injected by FXMLLoader

    //  SOS
    @FXML
    private Button SoS_Btn;
    //  SOS

    private Stage stage;
    private User loggedInManager = null;
    private User selectedMember = null;
    ArrayList<User> membersArrayList; // from DB
    ArrayList<String> membersInfoString; // for listView

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


    @FXML
    void BackToManagerHome(ActionEvent event) {
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow(); // first time stage takes value
        GuiCommon guiCommon = GuiCommon.getInstance();
        ManagerHomePageBoundary managerHomePageBoundary = (ManagerHomePageBoundary) guiCommon.displayNextScreen("ManagerHomePage.fxml",
                "Manager Home Page", stage, true);  // Example for opening new screen
        managerHomePageBoundary.setLoggedInUser(loggedInManager);
        EventBus.getDefault().unregister(this);
    }

    @FXML
    void showOpenedRequests(ActionEvent event) {

        stage = (Stage) ((Node)event.getSource()).getScene().getWindow(); // first time stage takes value

        //get selected line and get the user from it then pass it over
        String selectedItem = membersLV.getSelectionModel().getSelectedItem();
        if(selectedItem != null){
            String memberID = extractUserId(selectedItem);
            /*flag = "opened";
            getUserByID(memberID);*/
            //return;
            Platform.runLater(() -> {
                GuiCommon guiCommon = GuiCommon.getInstance();
                MemberOpenedTasks memberOpenedTasks = (MemberOpenedTasks) guiCommon.displayNextScreen("MemberOpenedTasks.fxml",
                        "opened Tasks", null, false);  // Example for opening new screen
                memberOpenedTasks.setLoggedInUser(loggedInManager);
                memberOpenedTasks.setCommunityMember(memberID);
                //flag = "";
            });
        }
        else{
            showNoSelectedItemMessage();
        }
    }

    private void getUserByID(String memberID) {
        int memberId = Integer.parseInt(memberID);

        Message message = new Message(1, LocalDateTime.now(), "get user by id",memberId);
        //System.out.println("task id="+taskId+"new status="+newStatus);
        try {
            SimpleClient.getClient("",0).sendToServer(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    String flag = ""; // flag to know which screen to open(volunteered or opened) when user is back in handle
    @FXML
    void showVolunteeredTasks(ActionEvent event) {

        stage = (Stage) ((Node)event.getSource()).getScene().getWindow(); // first time stage takes value

        //get selected line and get the user from it then pass it over
        String selectedItem = membersLV.getSelectionModel().getSelectedItem();
        if(selectedItem != null){
            String memberID = extractUserId(selectedItem);
            /*flag = "volunteered";
            getUserByID(memberID);*/
            //return;
            Platform.runLater(() -> {
                GuiCommon guiCommon = GuiCommon.getInstance();
                MemberVolunteeredTasks memberVolunteeredTasks = (MemberVolunteeredTasks) guiCommon.displayNextScreen("MemberVolunteeredTasks.fxml",
                        "Volunteered Tasks", null, false);  // Example for opening new screen
                memberVolunteeredTasks.setLoggedInUser(loggedInManager);
                memberVolunteeredTasks.setCommunityMember(memberID);
                //flag = "";
            });
        }
        else{
            showNoSelectedItemMessage();
        }
    /*
        System.out.println("user of "+memberID+" is null")
        System.out.println("after ExtractUserID Id = "+memberID);
        System.out.println(selectedMember);*/
    }
    private void showNoSelectedItemMessage() {
        // Handle the case when no task is selected
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Select a Community Member");
        alert.setHeaderText(null);
        alert.setContentText("You need to select a community member");

        // Show the alert dialog
        alert.showAndWait();
    }

    public static String extractUserId(String info) {
        // Split the info string by comma and whitespace
        String[] parts = info.split(",\\s*");

        // Check if there are enough parts and return the user ID (assuming it's the first part)
        if (parts.length > 0) {
            return parts[0].trim(); // Trim to remove any leading or trailing spaces
        } else {
            return ""; // Or handle the case where the info string format is incorrect
        }
    }

    @Subscribe
    public void handleTasksEvent(MessageEvent event){
        Message handledMessage=event.getMessage();
        if(event.getMessage().getMessage().equals("get all community users: Done")) {
            membersArrayList = (ArrayList<User>) event.getMessage().getData();
            membersArrayList.sort(Comparator.comparing(User::getUser_id));
            if (!membersArrayList.isEmpty()){
                Platform.runLater(() -> {
                    membersInfoString = getUsersInfoString(membersArrayList);
                    membersLV.getItems().addAll(membersInfoString);
                });
            }

        }
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnBack != null : "fx:id=\"btnBack\" was not injected: check your FXML file 'CommunityMembers.fxml'.";
        assert btnRequests != null : "fx:id=\"btnRequests\" was not injected: check your FXML file 'CommunityMembers.fxml'.";
        assert btnVolunteered != null : "fx:id=\"btnVolunteered\" was not injected: check your FXML file 'CommunityMembers.fxml'.";
        assert membersLV != null : "fx:id=\"membersLV\" was not injected: check your FXML file 'CommunityMembers.fxml'.";

        EventBus.getDefault().register(this);

    }

    private void getCommunityMembers(int communityID) {

        Message message = new Message(1, LocalDateTime.now(), "get all community users",
                                                                            communityID);
        //System.out.println("task id="+taskId+"new status="+newStatus);
        try {
            System.out.println("inside Community Members getting all community members");
            SimpleClient.getClient("",0).sendToServer(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ArrayList<String> getUsersInfoString(ArrayList<User> membersArrayList) {
        if(membersArrayList == null) return null;
        ArrayList<String> infoString = new ArrayList<>();
        for(User user : membersArrayList){
            String info = user.getUser_id()+", "+user.getUser_name()+", "+user.getFirst_name()
                    +", "+user.getLast_name();
            infoString.add(info);
        }
        return infoString;
    }

    public void setLoggedInUser(User loggedInUser) {
        this.loggedInManager = loggedInUser;
        getCommunityMembers(loggedInUser.getCommunityId());
    }


 /*   @FXML
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("**************************************************************");
        if (loggedInManager != null) {
         *//*   getCommunityMembers(loggedInManager.getCommunityId());
            membersInfoString = getUsersInfoString(membersArrayList);
            System.out.println("**************************************************************");
            System.out.println(membersArrayList);
            System.out.println(membersInfoString);
            System.out.println("**************************************************************");
            // Convert the ArrayList to an ObservableList
            ObservableList<String> observableList = FXCollections.observableArrayList(membersInfoString);

            membersLV.setItems(observableList);*//*
            System.out.println("*********************************HII*****************************");
           *//* getCommunityMembers(loggedInManager.getCommunityId());
            membersInfoString = getUsersInfoString(membersArrayList);
            membersLV.getItems().addAll(membersInfoString);*//*

        }


    }*/
}
