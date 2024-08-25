package gsix.ATIS.client.user;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import gsix.ATIS.client.CommunityMessageController;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class SendMessageToManager {
    private User loggedInUser=null;
    private int communityID=0;
    private String managerID=null;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;
    @FXML // fx:id="message_TF"
    private TextField message_TF; // Value injected by FXMLLoader

    @FXML // fx:id="back_Btn"
    private Button back_Btn; // Value injected by FXMLLoader

    @FXML // fx:id="send_Btn"
    private Button send_Btn; // Value injected by FXMLLoader
    private Stage stage;
    @FXML // fx:id="inProcess_LV"
    private ListView<String> inProcess_LV; // Value injected by FXMLLoader
    @FXML // fx:id="showTasks_Btn"
    private Button showTasks_Btn; // Value injected by FXMLLoader

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

    @FXML
    void BackToUserHomePage(ActionEvent event) {
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
    void showUnfinishedTasks(ActionEvent event) {
        if(loggedInUser!=null) {
            String userId = this.loggedInUser.getUser_id();
            // Initialize the ListView with available tasks
            getUnfinishedTasks(userId); //
        }
        else{
            System.out.println("LoggedInUser is null in ShowMessages showUnfinishedTasks");
        }

    }*/

    private void getUnfinishedTasks(String userId) {
        Message message = new Message(1, LocalDateTime.now(), "get unfinished tasks", userId);
        System.out.println("ShowMessages: Sent Messages");
        try {
            SimpleClient.getClient("",0).sendToServer(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void showEmptyMessageBoxAlert() {

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Empty Message");
        alert.setHeaderText(null);
        alert.setContentText("Please enter a message before sending.");
        alert.showAndWait();
    }
    @FXML
    void SendMessageForManager(ActionEvent event) {
        //get the message from the text field
        String givenMessage=message_TF.getText();

        if (givenMessage.isEmpty()) {
            // If the message is empty, show an alert to the user
            showEmptyMessageBoxAlert();
            return; // Exit the method, don't proceed further
        }
        communityID= loggedInUser.getCommunityId();
        //updating that the task is now Done and not in process
        // Get the selected task from the ListView
        String selectedTaskInfo = inProcess_LV.getSelectionModel().getSelectedItem();
        if (selectedTaskInfo != null) {
            // Parse the selected task information to get the task ID
            int taskId = extractTaskId(selectedTaskInfo);
            givenMessage="[Task Done notification]: Task " +taskId
                +"\nContent: "+ givenMessage;
            Task taskToUpdate = new Task(taskId, loggedInUser.getUser_id(), givenMessage);
            Message message2 = new Message(1, LocalDateTime.now(), "Send Volunteering Task End and Update To Done", taskToUpdate);
            System.out.println("Send to Manager getting Manager id");
            try {
                SimpleClient.getClient("", 0).sendToServer(message2);
            } catch (Exception e) {
                System.out.println("Error:  " + e.getMessage());
            }

            //open a new page
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // first time stage takes value
            GuiCommon guiCommon = GuiCommon.getInstance();
            UserHomePageBoundary userHomePage = (UserHomePageBoundary) guiCommon.displayNextScreen("UserHomePage.fxml",
                    "Community User Home Page", stage, true);  // Example for opening new screen
            userHomePage.setLoggedInUser(loggedInUser);
            EventBus.getDefault().unregister(this);
        }else {
            // Handle the case when no task is selected
            System.out.println("Please select a task to pick.");
            showEmptyMessageBoxAlert();
        }
    }
    private void updateTaskStatus(int taskId, String status) {
        // Implement the logic to update the task status in the database
        // Example: You may use JDBC or an ORM framework to execute an update query
        // Example query: UPDATE tasks SET status = 'pending' WHERE id = taskId;
        String info = taskId +","+ status;
        Message message = new Message(1, LocalDateTime.now(), "update task status",info);
        System.out.println("Volunteer:before send to server *updateTaskStatus* command");
        try {
            SimpleClient.getClient("",0).sendToServer(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int extractTaskId(String taskInfo) {
        // Split the taskInfo string based on comma and colon to extract the task ID
        String[] parts = taskInfo.split(","); // Split by comma
        for (String part : parts) {
            // Further split each part based on colon to check for "Task ID"
            String[] keyValue = part.trim().split(":");
            if (keyValue.length == 2 && keyValue[0].trim().equals("Task ID")) {
                // Parse and return the task ID
                return Integer.parseInt(keyValue[1].trim());
            }
        }
        // Return 0 if task ID is not found (or handle error appropriately)
        return 0;
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert back_Btn != null : "fx:id=\"back_Btn\" was not injected: check your FXML file 'Untitled'.";
        assert inProcess_LV != null : "fx:id=\"inProcess_LV\" was not injected: check your FXML file 'Untitled'.";
        assert message_TF != null : "fx:id=\"message_TF\" was not injected: check your FXML file 'Untitled'.";
        assert send_Btn != null : "fx:id=\"send_Btn\" was not injected: check your FXML file 'Untitled'.";
        assert showTasks_Btn != null : "fx:id=\"showTasks_Btn\" was not injected: check your FXML file 'Untitled'.";

        EventBus.getDefault().register(this);
    }
    @Subscribe
    public void handleTasksEvent(MessageEvent event){
        Message handledMessage=event.getMessage();
        //ADDED BY AYAL
        System.out.println("im inside SendToManager handle tasks");
        if(handledMessage.getMessage().equals("manager id is here")){
            System.out.println("im inside SendToManager handle tasks manager id is here");
            //get the manager id from the handled message
            managerID=(String)handledMessage.getData();

            //get the message from the text field
            String message="[Task Done notification]: Task ";
            String selectedTaskInfo = inProcess_LV.getSelectionModel().getSelectedItem();
            if (selectedTaskInfo != null) {
                int taskId = extractTaskId(selectedTaskInfo);
                message += taskId +"\nContent: "+ message_TF.getText();
            }
            //sending messages via CommunityMessageController
            System.out.println("Sending message to Manager about Done volunteering to task. \nMesaage: "+message);
            CommunityMessageController.send(loggedInUser.getUser_id(),managerID,message);


            // Display a pop-up message indicating that the message was sent to the manager
            // Display a pop-up message indicating that the message was sent to the manager
            Platform.runLater(() -> showAlert("Message Sent", "The message was successfully sent to the manager."));


        }
        if(handledMessage.getMessage().equals("get unfinished tasks: Done")){
            List<Task> unfinishedTasks=(List<Task>) event.getMessage().getData();
            unfinishedTasks.sort(Comparator.comparing(Task::getTime).reversed());
            System.out.println("I am handling theunfinished tasks being brought back from eventbus in Send to manager class");
            List<String> tasks_info = TasksController.getTasksInfo(unfinishedTasks);
            System.out.println(tasks_info);
            // Update ListView with received tasks
            Platform.runLater(() -> {
                inProcess_LV.getItems().clear(); // Clear existing items
                ObservableList<String> observableTasks = FXCollections.observableArrayList(tasks_info);
                inProcess_LV.setItems(observableTasks); // Add received tasks
            });
        }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public  void setLoggedInUser(User loggedUser){

        this.loggedInUser = loggedUser;
        String userID = loggedUser.getUser_id();
        getUnfinishedTasks(userID);
    }

}
