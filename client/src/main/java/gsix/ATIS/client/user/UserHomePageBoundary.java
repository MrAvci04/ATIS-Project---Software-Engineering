package gsix.ATIS.client.user;


import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;


import gsix.ATIS.client.SimpleClient;
import gsix.ATIS.client.TaskViewController;

import gsix.ATIS.client.common.GuiCommon;
import gsix.ATIS.client.common.MessageEvent;
import gsix.ATIS.client.common.SosBoundary;
import gsix.ATIS.client.login.LoginFrameBoundary;
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
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class UserHomePageBoundary {
    private Stage stage;
    private User loggedInUser;


    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    //  SOS
    @FXML
    private Button SoS_Btn;
    //  SOS

    @FXML // fx:id="message_to_manager_Btn"
    private Button message_to_manager_Btn; // Value injected by FXMLLoader

    @FXML // fx:id="messages_inbox_Btn"
    private Button messages_inbox_Btn; // Value injected by FXMLLoader

    @FXML // fx:id="my_tasks_Btn"
    private Button my_tasks_Btn; // Value injected by FXMLLoader

    @FXML // fx:id="open_request_Btn"
    private Button open_request_Btn; // Value injected by FXMLLoader

    @FXML // fx:id="volunteer_Btn"
    private Button volunteer_Btn; // Value injected by FXMLLoader

    @FXML // fx:id="logOut"
    private Button logOut; // Value injected by FXMLLoader

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
    void MessageToManager(ActionEvent event) {
        //open a new page
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow(); // first time stage takes value
        GuiCommon guiCommon = GuiCommon.getInstance();
        SendMessageToManager sendMessageToManagerPage = (SendMessageToManager) guiCommon.displayNextScreen("SendMessageToManager.fxml",
                "Community User Send Message to Manager Page", stage, true);  // Example for opening new screen

        sendMessageToManagerPage.setLoggedInUser(loggedInUser);
        EventBus.getDefault().unregister(this);

    }

    @FXML
    void MessagesInbox(ActionEvent event) {
        //open a new page
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow(); // first time stage takes value
        GuiCommon guiCommon = GuiCommon.getInstance();
        ShowMessageBoxController showMessageBoxControllerPage = (ShowMessageBoxController) guiCommon.displayNextScreen("ShowMessageBoxPage.fxml",
                "Community User Show Message Box Page", stage, true);  // Example for opening new screen

        showMessageBoxControllerPage.setLoggedInUser(loggedInUser);
        EventBus.getDefault().unregister(this);

    }

    @FXML
    void MyTasks(ActionEvent event) {
        //open a new page
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow(); // first time stage takes value
        GuiCommon guiCommon = GuiCommon.getInstance();
        MyTasksPage myTasksPage = (MyTasksPage) guiCommon.displayNextScreen("MyTasksPage.fxml",
                "Community User My Tasks Page", stage, true);  // Example for opening new screen

        myTasksPage.setLoggedInUser(loggedInUser);
        EventBus.getDefault().unregister(this);

    }

    @FXML
    void OpenRequest(ActionEvent event) {

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        //System.out.println("stam");
        GuiCommon guiCommon = GuiCommon.getInstance();
        OpenRequestBoundary openRequestBoundary = (OpenRequestBoundary) guiCommon.displayNextScreen
                ("OpenRequest.fxml", "Open Help Request", stage, true);
        openRequestBoundary.setRequesterInfo(loggedInUser);
        EventBus.getDefault().unregister(this);
        //stage.close();
    }

    @FXML
    void Volunteer(ActionEvent event) {
        //open a new page
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow(); // first time stage takes value
        GuiCommon guiCommon = GuiCommon.getInstance();
        Volunteer volunteerPage = (Volunteer) guiCommon.displayNextScreen("Volunteer.fxml",
                "Community User Volunteer Page", stage, true);  // Example for opening new screen

        volunteerPage.setLoggedInUser(loggedInUser);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void handleSomeEvent(MessageEvent event) {
        if (event.getMessage().getMessage().equals("open request: Done")) {
            Platform.runLater(() -> {
                Task dbUpdatedTask = (Task) event.getMessage().getData();
                //GuiCommon.popUp(dbUpdatedTask.toString() +"\n Task opened successfully, pending for Manager approve!");
            });
        }
        /**
         * in this event handler message we get an overdue task (no volunteer yet)
         * where volunteer_id is the community_id the task belongs to
         **/
        if (event.getMessage().getMessage().equals("notify overdue task")) {
            System.out.println("***** We should get no volunteer pop up *****************************************888");
            Task overDueTask = (Task) event.getMessage().getData();
            int overDueTask_communityID = Integer.parseInt(overDueTask.getVolunteer_id());
            if (loggedInUser.getCommunityId() == overDueTask_communityID && loggedInUser.getUser_id().equals(overDueTask.getRequester_id())) {
                Platform.runLater(() -> {
                    GuiCommon.popUp("community members have been notified about your yet unassisted task "+ overDueTask.getTask_id());
                });
            }
            if (loggedInUser.getCommunityId() == overDueTask_communityID && !loggedInUser.getUser_id().equals(overDueTask.getRequester_id())) {
                Platform.runLater(() -> {
                    GuiCommon.popUp("Task of ID=" + overDueTask.getTask_id()+" is still unassisted yet");
                });
            }
        }
    }

    @FXML
    void LogOut(ActionEvent event) {
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow(); // first time stage takes value
        //ask server to change logged_in in database back to 0 since he is logging out
        Message message = new Message(1, LocalDateTime.now(), "log out",loggedInUser);
        //System.out.println("task id="+taskId+"new status=");
        try {
            SimpleClient.getClient("",0).sendToServer(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        GuiCommon guiCommon = GuiCommon.getInstance();
        /*LoginFrameBoundary loginFrameBoundary = (LoginFrameBoundary)*/
        guiCommon.displayNextScreen("LoginForm.fxml",
                "Login Screen", stage, true);  // Example for opening new screen
        EventBus.getDefault().unregister(this);
    }

    public void setLoggedInUser(User loggedUser) {
        this.loggedInUser = loggedUser;
    }

    @FXML
        // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert SoS_Btn != null : "fx:id=\"SoS_Btn\" was not injected: check your FXML file 'UserHomePage.fxml'.";
        assert message_to_manager_Btn != null : "fx:id=\"message_to_manager_Btn\" was not injected: check your FXML file 'UserHomePage.fxml'.";
        assert messages_inbox_Btn != null : "fx:id=\"messages_inbox_Btn\" was not injected: check your FXML file 'UserHomePage.fxml'.";
        assert my_tasks_Btn != null : "fx:id=\"my_tasks_Btn\" was not injected: check your FXML file 'UserHomePage.fxml'.";
        assert open_request_Btn != null : "fx:id=\"open_request_Btn\" was not injected: check your FXML file 'UserHomePage.fxml'.";
        assert volunteer_Btn != null : "fx:id=\"volunteer_Btn\" was not injected: check your FXML file 'UserHomePage.fxml'.";



        EventBus.getDefault().register(this);
    }

}
