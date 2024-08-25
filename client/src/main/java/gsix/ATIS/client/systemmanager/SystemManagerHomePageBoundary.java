package gsix.ATIS.client.systemmanager;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import org.greenrobot.eventbus.EventBus;
import gsix.ATIS.client.SimpleClient;
import gsix.ATIS.client.common.GuiCommon;
import gsix.ATIS.entities.Message;
import gsix.ATIS.entities.User;
import org.hibernate.sql.Update;

import java.io.IOException;
import java.time.LocalDateTime;

public class SystemManagerHomePageBoundary {

    private Stage stage;
    private User loggedInUser;

    @FXML
    private Button logoutButton;
    @FXML
    private Button ogoutButton;
    @FXML
    private Button ltButton;

    @FXML
    void LogOut(ActionEvent event) {
        // Get the current stage from the event source
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();

        // Create a logout message and send it to the server
        Message message = new Message(1, LocalDateTime.now(), "log out", loggedInUser);
        try {
            SimpleClient.getClient("", 0).sendToServer(message);
        } catch (IOException e) {
            e.printStackTrace();
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

   /* @FXML
    void AddComBtn(ActionEvent event) {
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        GuiCommon guiCommon = GuiCommon.getInstance();
        guiCommon.displayNextScreen("AddComPage.fxml", "Add Community Page", stage, true);



    }*/

    @FXML
    void crtUser(ActionEvent event)
    {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Get the singleton instance of GuiCommon
        GuiCommon guiCommon = GuiCommon.getInstance();

        // Display the new screen and cast it to the appropriate controller
        CrUsPageBoundary rComPageBoundary = (CrUsPageBoundary) guiCommon.displayNextScreen(
                "CrUsPage.fxml",
                "Create User Page",
                stage, // pass the current stage
                true  // close the current stage and open the new one
        );
        rComPageBoundary.setLoggedInUser(loggedInUser);
    }

    @FXML
    void delUs(ActionEvent event)
    {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Get the singleton instance of GuiCommon
        GuiCommon guiCommon = GuiCommon.getInstance();

        // Display the new screen and cast it to the appropriate controller
        DelUsPageBoundary delsPageBoundary = (DelUsPageBoundary) guiCommon.displayNextScreen(
                "DelUsPage.fxml",
                "Delete User Page",
                stage, // pass the current stage
                true  // close the current stage and open the new one
        );
        delsPageBoundary.setLoggedInUser(loggedInUser);
    }
    @FXML
    void UpComBtn(ActionEvent event) {
        // Obtain the current stage
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Get the singleton instance of GuiCommon
        GuiCommon guiCommon = GuiCommon.getInstance();

        // Display the new screen and cast it to the appropriate controller
        UpdateComPageBoundary upComPageBoundary = (UpdateComPageBoundary) guiCommon.displayNextScreen(
                "UpdateComPage.fxml",
                "Update Community Page",
                stage, // pass the current stage
                true  // close the current stage and open the new one
        );
        upComPageBoundary.setLoggedInUser(loggedInUser);
    }
    @FXML
    void UpManbtn(ActionEvent event) {
        // Obtain the current stage
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Get the singleton instance of GuiCommon
        GuiCommon guiCommon = GuiCommon.getInstance();

        UpdMangPageBoundary crUsPageBoundary = (UpdMangPageBoundary) guiCommon.displayNextScreen(
                "UpdMangPage.fxml",
                "Update Manager Page",
                stage,
                true
        );
        crUsPageBoundary.setLoggedInUser(loggedInUser);
    }
    @FXML
    void UpUsbtn(ActionEvent event) {
        // Obtain the current stage
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Get the singleton instance of GuiCommon
        GuiCommon guiCommon = GuiCommon.getInstance();

        UpdUserPageBoundary crUsPageBoundary = (UpdUserPageBoundary) guiCommon.displayNextScreen(
                "UpdUserPage.fxml",
                "Update User Page",
                stage,
                true
        );
        crUsPageBoundary.setLoggedInUser(loggedInUser);
    }
    @FXML
    void AddComBtn(ActionEvent event) {
        // Obtain the current stage
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Get the singleton instance of GuiCommon
        GuiCommon guiCommon = GuiCommon.getInstance();

        // Display the new screen and cast it to the appropriate controller
        AddComPageBoundary addComPageBoundary = (AddComPageBoundary) guiCommon.displayNextScreen(
                "AddComPage.fxml",
                "Add Community Page",
                stage, // pass the current stage
                true  // close the current stage and open the new one
        );

        // If you need to pass data to the new screen, you can do it here
        addComPageBoundary.setLoggedInUser(loggedInUser);
    }


    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
