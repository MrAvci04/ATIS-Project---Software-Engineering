package gsix.ATIS.client.systemmanager;

import gsix.ATIS.client.SimpleClient;
import gsix.ATIS.client.common.GuiCommon;
import gsix.ATIS.client.common.MessageEvent;
import gsix.ATIS.entities.User;
import gsix.ATIS.entities.Message;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class DelUsPageBoundary {
    private User loggedInUser;

    @FXML
    private ListView<User> userListView;

    @FXML
    private Button deleteUserBtn;

    @FXML
    private Button backBtn;

    @FXML
    void initialize() {
        EventBus.getDefault().register(this);
        loadUsers();
    }

    private void loadUsers() {
        Message message = new Message(1, LocalDateTime.now(), "get all users", "");
        try {
            SimpleClient.getClient("", 0).sendToServer(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void handleTasksEvent(MessageEvent event) {
        Message handledMessage = event.getMessage();
        if ("get all users: Done".equals(handledMessage.getMessage())) {
            List<User> data = (List<User>) handledMessage.getData();
            Platform.runLater(() -> {
                ObservableList<User> observableUsers = FXCollections.observableArrayList(data);
                userListView.setItems(observableUsers);
            });
        }
    }

    @FXML
    void deleteUser() {
        User selectedUser = userListView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            Message message = new Message(1, LocalDateTime.now(), "delete community user", selectedUser.getUser_id());
            try {
                SimpleClient.getClient("", 0).sendToServer(message);
                // Confirm deletion
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("User Deleted");
                alert.setHeaderText(null);
                alert.setContentText("User has been deleted successfully.");
                alert.showAndWait();
                // Refresh the ListView
                loadUsers();
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Deletion Error");
                alert.setHeaderText(null);
                alert.setContentText("An error occurred while deleting the user.");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select a user to delete.");
            alert.showAndWait();
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
    }
}
