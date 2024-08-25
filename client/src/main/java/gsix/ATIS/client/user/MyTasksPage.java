package gsix.ATIS.client.user;

/**
 * Sample Skeleton for 'Untitled' Controller Class
 */

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
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
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MyTasksPage {
    private User loggedInUser=null;
    private Stage stage;


    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="Volunteered_LV"
    private ListView<String> Volunteered_LV; // Value injected by FXMLLoader

    @FXML // fx:id="back_Btn"
    private Button back_Btn; // Value injected by FXMLLoader
    @FXML // fx:id="requested_Btn"
    private Button requested_Btn; // Value injected by FXMLLoader
    @FXML // fx:id="volunteered_Btn"
    private Button volunteered_Btn; // Value injected by FXMLLoader

    @FXML // fx:id="requested_LV"
    private ListView<String> requested_LV; // Value injected by FXMLLoader

    @FXML
    private ComboBox<String> openedTasks_cmbBox;


    @FXML
    private ComboBox<String> voluntTasks_cmbBox;

    //  SOS
    @FXML
    private Button SoS_Btn;
    //  SOS

    private final String[] pickOpenStatus = {"Request", "Pending","in process","Done","Declined"};
    private String desiredOpenStatusToView ="Request";
    private final String[] pickVolunteeredStatus = {"in process","Done"};
    private String desiredVolunteeredStatusToView ="Done";

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

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert Volunteered_LV != null : "fx:id=\"Volunteered_LV\" was not injected: check your FXML file 'Untitled'.";
        assert back_Btn != null : "fx:id=\"back_Btn\" was not injected: check your FXML file 'Untitled'.";
        assert requested_LV != null : "fx:id=\"requested_LV\" was not injected: check your FXML file 'Untitled'.";
        assert requested_Btn != null : "fx:id=\"requested_Btn\" was not injected: check your FXML file 'Untitled'.";
        assert volunteered_Btn != null : "fx:id=\"volunteered_Btn\" was not injected: check your FXML file 'Untitled'.";


        EventBus.getDefault().register(this);

        openedTasks_cmbBox.getItems().addAll(pickOpenStatus);
        openedTasks_cmbBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            desiredOpenStatusToView = newValue;
            getRequestedTasks(loggedInUser.getUser_id());
        });
        voluntTasks_cmbBox.getItems().addAll(pickVolunteeredStatus);
        voluntTasks_cmbBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            desiredVolunteeredStatusToView = newValue;
            getVolunteeredTasks(loggedInUser.getUser_id());
        });
    }
    /*@FXML
    void showRequestedTasks(ActionEvent event) {
        if(loggedInUser!=null) {
            String userId = this.loggedInUser.getUser_id();
            System.out.println("im inside showRequestedTasks after pressing the button and loggedInUsere isnt null");
            // Initialize the ListView with available tasks
            getRequestedTasks(userId); //
        }
        else{
            System.out.println("LoggedInUser is null in MyTasksPage showRequestedTasks");
        }

    }*/

    private void getRequestedTasks(String userId) {
        /** CHANGE NEXT REQUEST STATUS TO COMBOBOX RESULT */
        Task dummyTask = new Task(userId,"dummy operation",desiredOpenStatusToView,0);

        Message message = new Message(1, LocalDateTime.now(), "get requested tasks", dummyTask);
        System.out.println("MyTasksPage:before send to server *Get Requested Tasks* command");
        try {
            SimpleClient.getClient("",0).sendToServer(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*@FXML
    void ShowVolunteeredTasks(ActionEvent event) {
        if(loggedInUser!=null) {
            String userId = this.loggedInUser.getUser_id();
            System.out.println("im inside showVolunteeredTasks after pressing the button and loggedInUsere isnt null");
            // Initialize the ListView with available tasks
            getVolunteeredTasks(userId); //
        }
        else{
            System.out.println("LoggedInUser is null in MyTasksPage showRequestedTasks");
        }

    }*/

    private void getVolunteeredTasks(String userId) {
        /** CHANGE NEXT REQUEST STATUS TO COMBOBOX RESULT */
        Task dummyTask = new Task(userId,"dummy operation",desiredVolunteeredStatusToView,0);

        Message message = new Message(1, LocalDateTime.now(), "get volunteered tasks", dummyTask);
        System.out.println("MyTasksPage:before send to server *Get Volunteered Tasks* command");
        try {
            SimpleClient.getClient("",0).sendToServer(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Subscribe
    public void handleTasksEvent(MessageEvent event){
        Message handledMessage=event.getMessage();
        //ADDED BY AYAL
        if(handledMessage.getMessage().equals("get requested tasks: Done")){
            List<Task> requestedTasks=(List<Task>) handledMessage.getData();
            requestedTasks.sort(Comparator.comparing(Task::getTime).reversed());
            System.out.println("I am handling the requested tasks being brought back from eventbus in Volunteer class");
            List<String> tasks_info = TasksController.getTasksInfo(requestedTasks);
            System.out.println(tasks_info);
            // Update ListView with received tasks
            Platform.runLater(() -> {
                requested_LV.getItems().clear(); // Clear existing items
                ObservableList<String> observableTasks = FXCollections.observableArrayList(tasks_info);
                requested_LV.setItems(observableTasks); // Add received tasks
            });
        }
        if(handledMessage.getMessage().equals("get volunteered tasks: Done")){
            List<Task> volunteeredTasks=(List<Task>) handledMessage.getData();
            volunteeredTasks.sort(Comparator.comparing(Task::getTime).reversed());
            System.out.println("I am handling the volunteered tasks being brought back from eventbus in Volunteer class");
            List<String> tasks_info = TasksController.getTasksInfo(volunteeredTasks);
            System.out.println(tasks_info);
            // Update ListView with received tasks
            Platform.runLater(() -> {
                Volunteered_LV.getItems().clear(); // Clear existing items
                ObservableList<String> observableTasks = FXCollections.observableArrayList(tasks_info);
                Volunteered_LV.setItems(observableTasks); // Add received tasks
            });
        }
        /*if(event.getMessage().getMessage().equals("open request: Done")) {
            getRequestedTasks(loggedInUser.getUser_id());
        }*/
        if(event.getMessage().getMessage().equals("start volunteering to task : Done")) {
            getRequestedTasks(loggedInUser.getUser_id());
            getVolunteeredTasks(loggedInUser.getUser_id());
        }
        if(event.getMessage().getMessage().equals("Send Volunteering Task End and Update To Done: Done")) {
            getRequestedTasks(loggedInUser.getUser_id());
            getVolunteeredTasks(loggedInUser.getUser_id());
        }
        if(event.getMessage().getMessage().equals("update task status Pending: Done")) {
            getRequestedTasks(loggedInUser.getUser_id());
            getVolunteeredTasks(loggedInUser.getUser_id());
        }
        if(event.getMessage().getMessage().equals("Decline Task and Send Decline message to Requester: Done")) {
            getRequestedTasks(loggedInUser.getUser_id());
            getVolunteeredTasks(loggedInUser.getUser_id());
        }
    }

    public void setLoggedInUser(User user) {

        this.loggedInUser=user;
        String userID = loggedInUser.getUser_id();
        //getRequestedTasks(userID);
        //getVolunteeredTasks(userID);
    }
}
