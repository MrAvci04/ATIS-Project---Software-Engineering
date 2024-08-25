/**
 * Sample Skeleton for 'ManagerHomePage.fxml' Controller Class
 */

package gsix.ATIS.client.manager;

import gsix.ATIS.client.SimpleClient;
import gsix.ATIS.client.common.GuiCommon;
import gsix.ATIS.client.user.ShowMessageBoxController;
import gsix.ATIS.client.common.MessageEvent;
import gsix.ATIS.client.common.SosBoundary;
import gsix.ATIS.client.login.LoginFrameBoundary;
import gsix.ATIS.entities.Message;
import gsix.ATIS.entities.Task;
import gsix.ATIS.entities.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.time.LocalDateTime;

public class ManagerHomePageBoundary {
    private Stage stage;
    private User loggedInUser;

    //  SOS
    @FXML
    private Button SoS_Btn;
    //  SOS

    @FXML // fx:id="btnSOSReports"
    private Button btnSOSReports; // Value injected by FXMLLoader

    @FXML // fx:id="msgToUser"
    private Button msgToUser; // Value injected by FXMLLoader

    @FXML // fx:id="RequestedTask"
    private Button RequestedTask; // Value injected by FXMLLoader

    @FXML // fx:id="viewMsges"
    private Button viewMsges; // Value injected by FXMLLoader

    @FXML // fx:id="btnViewMembers"
    private Button btnViewMembers; // Value injected by FXMLLoader

    @FXML // fx:id="logOut"
    private Button logOut; // Value injected by FXMLLoader

    public void setLoggedInUser(User loggedUser) {
        this.loggedInUser = loggedUser;
    }


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
    void ViewSOSReports(ActionEvent event) {
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow(); // first time stage takes value
        GuiCommon guiCommon = GuiCommon.getInstance();
        SOSReports sosReports = (SOSReports) guiCommon.displayNextScreen
                ("SOSReports.fxml", "SOS Reports", stage, true);  // Example for opening new screen

        sosReports.setLoggedInUser(loggedInUser);
        EventBus.getDefault().unregister(this);
    }

    @FXML
    void ViewCommunityMembers(ActionEvent event) {
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow(); // first time stage takes value
        GuiCommon guiCommon = GuiCommon.getInstance();
        CommunityMembers communityMembers = (CommunityMembers) guiCommon.displayNextScreen
                ("CommunityMembers.fxml", "Community Members", stage, true);  // Example for opening new screen

        communityMembers.setLoggedInUser(loggedInUser);
        EventBus.getDefault().unregister(this);
    }

    @FXML
    void ViewMessages(ActionEvent event) {

        stage = (Stage) ((Node)event.getSource()).getScene().getWindow(); // first time stage takes value
        GuiCommon guiCommon = GuiCommon.getInstance();

        ///1. "the name of java file that has the go back etc"
        /// ShowMessagesViewer
        ///2. "1 adding page"
        /// ShowMessagesViewerPage
        ///3. "the fxml file (not 1 nor 2)"
        /// MessagesViewerPage

        ShowMessagesViewer ShowMessagesViewerPage =
                (ShowMessagesViewer) guiCommon.displayNextScreen
                        ("MessagesViewerPage.fxml",
                "Manager Messages Viewer Page",
                                stage, true);
        // Example for opening new screen

        ShowMessagesViewerPage.setLoggedInUser(loggedInUser);
        EventBus.getDefault().unregister(this);
    }

    @FXML
    void ViewRequestedTasks(ActionEvent event) {
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow(); // first time stage takes value
        GuiCommon guiCommon = GuiCommon.getInstance();
        RequestedTasks requestedTasks = (RequestedTasks) guiCommon.displayNextScreen("RequestedTasks.fxml",
                "Community's Requested Tasks Page", null, false);  // Example for opening new screen
        requestedTasks.setLoggedInUser(loggedInUser);
        stage.close();
        EventBus.getDefault().unregister(this);
    }

    @FXML
    void logOut(ActionEvent event) {
        // Get the current stage
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        //ask server to change logged_in in database back to 0 since he is logging out
        Message message = new Message(1, LocalDateTime.now(), "log out",loggedInUser);
        //System.out.println("task id="+taskId+"new status=");
        try {
            SimpleClient.getClient("",0).sendToServer(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Set an event handler for the stage's close request
        stage.setOnCloseRequest(e -> {
            // Custom logic before the stage closes
            System.out.println("Stage is closing. Unregistering from EventBus.");

            // Unregister from EventBus to avoid memory leaks
            if (EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().unregister(this);
            }
        });

        // Display the login screen
        GuiCommon guiCommon = GuiCommon.getInstance();
        guiCommon.displayNextScreen("LoginForm.fxml", "Login Screen", stage, true);
        EventBus.getDefault().unregister(this);

    }
    @FXML
        // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        // Register with EventBus
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }


    }
    @Subscribe
    public void handleSomeEvent(MessageEvent event) {
        if (event.getMessage().getMessage().equals("Handling task")) {
            Platform.runLater(() -> {

            });
        }
    }



}
