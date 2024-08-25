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
import gsix.ATIS.entities.Message;
import gsix.ATIS.entities.Task;
import gsix.ATIS.entities.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MemberOpenedTasks {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="btnBack"
    private Button btnBack; // Value injected by FXMLLoader

    @FXML // fx:id="openedLV"
    private ListView<String> openedLV; // Value injected by FXMLLoader


    @FXML
    private ComboBox<String> desired_cmbBox;
    private Stage stage;
    private User loggedInManager = null;
    private String communityMemberID = null;
    ArrayList<Task> memberOpenedTasksArrayList; // from DB
    ArrayList<String> memberOpenedTasksInfoString; // for listView

    private final String[] pickStatus = {"Request", "Pending","in process","Done","Declined"};
    private String desiredStatusToView ="Request";

    public void setLoggedInUser(User loggedInManager) {
        this.loggedInManager = loggedInManager;
    }
    public void setCommunityMember(String communityMemberID) {
        this.communityMemberID = communityMemberID;
        //getRequestedTasks(communityMemberID);
    }

    private void showNoTasksToViewAlert() {
        // Handle the case when no task is selected
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("No Available Tasks");
        alert.setHeaderText(null);
        alert.setContentText("There Are No Tasks To View");

        // Show the alert dialog
        alert.showAndWait();
    }

    @FXML
    void BackToCommunityMembersScreen(ActionEvent event) {
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow(); // first time stage takes value
        GuiCommon guiCommon = GuiCommon.getInstance();
        /*CommunityMembers communityMembers = (CommunityMembers) guiCommon.displayNextScreen("CommunityMembers.fxml",
                "Community Members", stage, true);  // Example for opening new screen
        communityMembers.setLoggedInUser(loggedInManager);
        EventBus.getDefault().unregister(this);*/
        stage.close();
        EventBus.getDefault().unregister(this);
    }

    private void getRequestedTasks(String userId) {
        Task dummyTask = new Task(userId,"dummy operation",desiredStatusToView,0);
        Message message = new Message(1, LocalDateTime.now(), "get requested tasks", dummyTask);
        //System.out.println("MyTasksPage:before send to server *Get Requested Tasks* command");
        try {
            SimpleClient.getClient("",0).sendToServer(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ArrayList<String> getOpenedTasksInfo(List<Task> tasks) {
        ArrayList<String> tasks_info = new ArrayList<>();
        for (Task task : tasks) {
            tasks_info.add("Task ID:" + task.getTask_id() + ", Help Request Description: " +
                    task.getRequested_operation() + ", Task Status: " + task.getStatus());
        }
        return tasks_info;
    }

    @Subscribe
    public void handleTasksEvent(MessageEvent event){
        Message handledMessage=event.getMessage();

        if(event.getMessage().getMessage().equals("get requested tasks: Done")) {
            openedLV.getItems().clear();
            memberOpenedTasksArrayList = (ArrayList<Task>) event.getMessage().getData();
            memberOpenedTasksArrayList.sort(Comparator.comparing(Task::getTime).reversed());
            if(!memberOpenedTasksArrayList.isEmpty()){
                memberOpenedTasksArrayList.sort(Comparator.comparing(Task::getTime).reversed());
                Platform.runLater(() -> {
                    memberOpenedTasksInfoString = (ArrayList<String>) getOpenedTasksInfo(memberOpenedTasksArrayList);
                    //openedLV.getItems().clear();
                    openedLV.getItems().addAll(memberOpenedTasksInfoString);
                });
            }else{
                Platform.runLater(this::showNoTasksToViewAlert);
            }

        }
        if(event.getMessage().getMessage().equals("open request: Done")) {
            getRequestedTasks(communityMemberID);
        }
        if(event.getMessage().getMessage().equals("start volunteering to task : Done")) {
            getRequestedTasks(communityMemberID);
        }
        if(event.getMessage().getMessage().equals("Send Volunteering Task End and Update To Done: Done")) {
            getRequestedTasks(communityMemberID);
        }
        if(event.getMessage().getMessage().equals("update task status Pending: Done")) {
            getRequestedTasks(communityMemberID);
        }
        if(event.getMessage().getMessage().equals("Decline Task and Send Decline message to Requester: Done")) {
            getRequestedTasks(communityMemberID);
        }
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnBack != null : "fx:id=\"btnBack\" was not injected: check your FXML file 'MemberOpenedTasks.fxml'.";
        assert openedLV != null : "fx:id=\"openedLV\" was not injected: check your FXML file 'MemberOpenedTasks.fxml'.";

        EventBus.getDefault().register(this);

        desired_cmbBox.getItems().addAll(pickStatus);
        desired_cmbBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            desiredStatusToView = newValue;
            getRequestedTasks(communityMemberID);
        });
    }

 /*   @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }*/

}