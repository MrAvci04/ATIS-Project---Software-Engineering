package gsix.ATIS.client.manager;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ResourceBundle;

import gsix.ATIS.client.SimpleClient;
import gsix.ATIS.client.TasksController;
import gsix.ATIS.client.common.GuiCommon;
import gsix.ATIS.client.common.MessageEvent;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MemberVolunteeredTasks {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="btnBack"
    private Button btnBack; // Value injected by FXMLLoader

    @FXML // fx:id="volunteeredLV"
    private ListView<String> volunteeredLV; // Value injected by FXMLLoader

    @FXML
    private ComboBox<String> desired_cmbBox;
    private Stage stage;
    private User loggedInManager = null;
    private String communityMemberID = null;
    ArrayList<Task> memberVolunteeredTasksArrayList; // from DB
    ArrayList<String> memberVolunteeredTasksInfoString; // for listView

    private final String[] pickStatus = {"in process","Done"};
    private String desiredStatusToView ="Done";

    public void setLoggedInUser(User loggedInManager) {
        this.loggedInManager = loggedInManager;
    }

    public void setCommunityMember(String communityMemberID) {
        this.communityMemberID = communityMemberID;
        //getVolunteeredTasks(communityMemberID);
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
    public void BackToCommunityMembersScreen(ActionEvent event) {
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow(); // first time stage takes value
        GuiCommon guiCommon = GuiCommon.getInstance();
        /*CommunityMembers communityMembers = (CommunityMembers) guiCommon.displayNextScreen("CommunityMembers.fxml",
                "Community Members", stage, true);  // Example for opening new screen
        communityMembers.setLoggedInUser(loggedInManager);
        EventBus.getDefault().unregister(this);*/
        stage.close();
        EventBus.getDefault().unregister(this);
    }

    private void getVolunteeredTasks(String userId) {
        Task dummyTask = new Task(userId,"dummy operation",desiredStatusToView,0);
        Message message = new Message(1, LocalDateTime.now(), "get volunteered tasks", dummyTask);
        //System.out.println("MyTasksPage:before send to server *Get Volunteered Tasks* command");
        try {
            SimpleClient.getClient("",0).sendToServer(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Subscribe
    public void handleTasksEvent(MessageEvent event){
        Message handledMessage=event.getMessage();
        if(event.getMessage().getMessage().equals("get volunteered tasks: Done")){
            volunteeredLV.getItems().clear();
            memberVolunteeredTasksArrayList = (ArrayList<Task>) event.getMessage().getData();
            memberVolunteeredTasksArrayList.sort(Comparator.comparing(Task::getTime).reversed());
            if(!memberVolunteeredTasksArrayList.isEmpty()){
                Platform.runLater(() -> {
                    memberVolunteeredTasksArrayList.sort(Comparator.comparing(Task::getTime).reversed());
                    memberVolunteeredTasksInfoString = (ArrayList<String>) TasksController.getTasksInfo(memberVolunteeredTasksArrayList);
                    //volunteeredLV.getItems().clear();
                    volunteeredLV.getItems().addAll(memberVolunteeredTasksInfoString);
                });
            }else{
                Platform.runLater(this::showNoTasksToViewAlert);
            }
        }
        if(event.getMessage().getMessage().equals("start volunteering to task : Done")) {
            getVolunteeredTasks(communityMemberID);
        }
        if(event.getMessage().getMessage().equals("Send Volunteering Task End and Update To Done: Done")) {
            getVolunteeredTasks(communityMemberID);
        }
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnBack != null : "fx:id=\"btnBack\" was not injected: check your FXML file 'MemberVolunteeredTasks.fxml'.";
        assert volunteeredLV != null : "fx:id=\"volunteeredLV\" was not injected: check your FXML file 'MemberVolunteeredTasks.fxml'.";

        EventBus.getDefault().register(this);
        desired_cmbBox.getItems().addAll(pickStatus);
        desired_cmbBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            desiredStatusToView = newValue;
            getVolunteeredTasks(communityMemberID);
        });
    }


/*    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }*/

}

