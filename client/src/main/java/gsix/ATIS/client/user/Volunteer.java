
/**
 * Sample Skeleton for 'Untitled' Controller Class
 */

package gsix.ATIS.client.user;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import gsix.ATIS.client.common.GuiCommon;
import gsix.ATIS.client.common.SosBoundary;
import gsix.ATIS.entities.TaskStatus;
import javafx.event.ActionEvent;

import gsix.ATIS.client.SimpleClient;
import gsix.ATIS.client.TasksController;
import gsix.ATIS.client.common.MessageEvent;
import gsix.ATIS.entities.Message;
import gsix.ATIS.entities.Task;
import gsix.ATIS.entities.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class Volunteer {

    private User loggedInUser=null;
    private Stage stage;


    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="availableTaskListView"
    private ListView<String> availableTaskListView; // Value injected by FXMLLoader

    @FXML // fx:id="back_Btn"
    private Button back_Btn; // Value injected by FXMLLoader

    /*@FXML // fx:id="choose_Btn"
    private Button choose_Btn; // Value injected by FXMLLoader*/
    @FXML
    private Button pickTask_Btn;
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

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert availableTaskListView != null : "fx:id=\"availableTaskListView\" was not injected: check your FXML file 'Untitled'.";
        assert back_Btn != null : "fx:id=\"back_Btn\" was not injected: check your FXML file 'Untitled'.";
        //assert choose_Btn != null : "fx:id=\"choose_Btn\" was not injected: check your FXML file 'Untitled'.";
        assert pickTask_Btn != null : "fx:id=\"pickTask_Btn\" was not injected: check your FXML file 'Volunteer.fxml'.";

        EventBus.getDefault().register(this);


    }
    @Subscribe
    public void handleTasksEvent(MessageEvent event){
        Message handledMessage=event.getMessage();
        //ADDED BY AYAL
        if(event.getMessage().getMessage().equals("get tasks for community: Done")){
            List<Task> communityTasks=(List<Task>) event.getMessage().getData();
            communityTasks.sort(Comparator.comparing(Task::getTime).reversed());
            System.out.println("I am handling the tasks for community being brought back from eventbus in Volunteer class");
            List<String> tasks_info = TasksController.getTasksInfo(communityTasks);
            System.out.println(tasks_info);
            // Update ListView with received tasks
            Platform.runLater(() -> {
                availableTaskListView.getItems().clear(); // Clear existing items
                ObservableList<String> observableTasks = FXCollections.observableArrayList(tasks_info);
                availableTaskListView.setItems(observableTasks); // Add received tasks
            });
        }
        if(event.getMessage().getMessage().equals("update task status Pending: Done")){
            getTasksForCommunity(loggedInUser.getUser_id());
        }
        if(event.getMessage().getMessage().equals("start volunteering to task : Done")){
            getTasksForCommunity(loggedInUser.getUser_id());
        }
    }
    @FXML
    private void backToUserHome(ActionEvent event) {
        // Implement logic to navigate back to the user's home page
        //open a new page
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow(); // first time stage takes value
        GuiCommon guiCommon = GuiCommon.getInstance();
        UserHomePageBoundary userHomePage = (UserHomePageBoundary) guiCommon.displayNextScreen("UserHomePage.fxml",
                "Community User Home Page", stage, true);  // Example for opening new screen
        userHomePage.setLoggedInUser(loggedInUser);
        EventBus.getDefault().unregister(this);
    }

    @FXML
    void pickTask(ActionEvent actionEvent) {
        //Implement logic to pick a Task
        // Get the selected task from the ListView
        String selectedTaskInfo = availableTaskListView.getSelectionModel().getSelectedItem();
        if (selectedTaskInfo != null) {
            // Parse the selected task information to get the task ID
            int taskId = extractTaskId(selectedTaskInfo);
            System.out.println("task id is : "+taskId);
            // Update the task status in the database
            //updateTaskStatus(taskId, "in process");
            updateTaskVolunteer(taskId,loggedInUser.getUser_id());

            // Show a pop-up message to indicate success
            showSuccessMessage();

            // Implement logic to navigate back to the user's home page
            //open a new page
            stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow(); // first time stage takes value
            GuiCommon guiCommon = GuiCommon.getInstance();
            UserHomePageBoundary userHomePage = (UserHomePageBoundary) guiCommon.displayNextScreen("UserHomePage.fxml",
                    "Community User Home Page", stage, true);  // Example for opening new screen
            userHomePage.setLoggedInUser(loggedInUser);
            EventBus.getDefault().unregister(this);
        } else {
            // Handle the case when no task is selected
            System.out.println("Please select a task to pick.");
        }
    }

    private void updateTaskVolunteer(int taskId, String volunteerID) {
        Task dummyTask=new Task(taskId,volunteerID);
        dummyTask.setStatus(TaskStatus.inProcess);
        Message message = new Message(1, LocalDateTime.now(), "start volunteering to task",dummyTask);
        System.out.println("Volunteer:before send to server *updateTaskVolunteer* command");
        try {
            SimpleClient.getClient("",0).sendToServer(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Method to display a pop-up message indicating success
    private void showSuccessMessage() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Task Picked");
        alert.setHeaderText(null);
        alert.setContentText("You have successfully volunteered for this task!");

        // Show the alert dialog
        alert.showAndWait();
    }

    // Method to extract the task ID from the task information string
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


/*    @FXML
    private void showTasks() {
        if(loggedInUser!=null) {
            String userId = this.loggedInUser.getUser_id();
            System.out.println("im inside showTasks after pressing the button and loggedInUsere isnt null");
            // Initialize the ListView with available tasks
            getTasksForCommunity(userId); //
        }
        else{
            System.out.println("LoggedInUser is null in Volunteer Do task line 81");
        }
    }*/
    public void getTasksForCommunity(String userId) {

        Message message = new Message(1, LocalDateTime.now(), "get tasks for community", userId);
        System.out.println("Volunteer:before send to server *Get Tasks for Community* command");
        try {
            SimpleClient.getClient("",0).sendToServer(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public void setLoggedInUser(User loggedInUser) {

        this.loggedInUser=loggedInUser;
        getTasksForCommunity(loggedInUser.getUser_id());
    }
}
