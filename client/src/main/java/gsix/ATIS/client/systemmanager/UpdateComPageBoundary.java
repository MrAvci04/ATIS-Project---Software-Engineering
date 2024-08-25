package gsix.ATIS.client.systemmanager;

import gsix.ATIS.client.SimpleClient;
import gsix.ATIS.client.common.GuiCommon;
import gsix.ATIS.client.common.MessageEvent;
import gsix.ATIS.entities.Community;
import gsix.ATIS.entities.Message;
import gsix.ATIS.entities.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class UpdateComPageBoundary {

    private Stage stage;
    private User loggedInUser;

    @FXML
    private Button back_Btn;

    @FXML
    private TextField userIdTF;

    @FXML
    private TextField firstNameTF;

    @FXML
    private TextField lastNameTF;

    @FXML
    private TextField userNameTF;

    @FXML
    private PasswordField passwordTF;

    @FXML
    private ComboBox<String> userTypeComboBox;

    @FXML
    private ComboBox<Integer> communityIdComboBox;

    @FXML
    private ListView<User> userListView;

    @FXML
    private Button addUserBtn;

    @FXML
    private Button deleteUserBtn;

    @FXML
    private Button updateManagerBtn;

    @FXML
    void initialize() {
        EventBus.getDefault().register(this);

        userTypeComboBox.getItems().addAll("community manager", "community user", "system manager");

        loadCommunities();
        loadUsers();
    }

    private void loadCommunities() {
        Message message = new Message(1, LocalDateTime.now(), "get all communities", "");
        try {
            SimpleClient.getClient("", 0).sendToServer(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadUsers() {
        Message message = new Message(1, LocalDateTime.now(), "get all users", "");
        try {
            SimpleClient.getClient("", 0).sendToServer(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Subscribe
    public void handleTasksEvent(MessageEvent event) {
        Message handledMessage = event.getMessage();
        if ("get all communities: Done".equals(handledMessage.getMessage())) {
            List<Community> data = (List<Community>) handledMessage.getData();
            Platform.runLater(() -> {
                communityIdComboBox.getItems().clear();
                for (Community community : data) {
                    communityIdComboBox.getItems().add(community.getCommunity_id());
                }
            });
        }
        if ("get all users: Done".equals(handledMessage.getMessage())) {
            List<User> data = (List<User>) handledMessage.getData();
            Platform.runLater(() -> {
                userListView.getItems().clear();
                ObservableList<User> observableUsers = FXCollections.observableArrayList(data);
                userListView.setItems(observableUsers);
            });
        }
        if ("update user community: Done".equals(handledMessage.getMessage()) ||
                "delete community user: Done".equals(handledMessage.getMessage()) ||
                "update community manager: Done".equals(handledMessage.getMessage())) {
            loadUsers(); // Refresh the user list after any operation
        }
    }

    @FXML
    void addUser() {
        User selectedUser = userListView.getSelectionModel().getSelectedItem();
        Integer selectedCommunityId = communityIdComboBox.getValue();
        if (selectedUser != null && selectedCommunityId != null) {
            selectedUser.setCommunity(selectedCommunityId);

            Message message = new Message(1, LocalDateTime.now(), "update user community", selectedUser);
            try {
                SimpleClient.getClient("", 0).sendToServer(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Error", "Please select a user and a community.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void deleteUser() {
        User selectedUser = userListView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            selectedUser.setCommunity(-1);  // Set to 0 or null to indicate no community

            Message message = new Message(1, LocalDateTime.now(), "update user community", selectedUser);
            try {
                SimpleClient.getClient("", 0).sendToServer(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Error", "Please select a user to delete.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void updateManager() {
        User selectedUser = userListView.getSelectionModel().getSelectedItem();
        Integer communityId = communityIdComboBox.getValue();

        if (selectedUser != null && communityId != null) {
            // Get the ID of the selected user
            int selectedUserCommunityId = selectedUser.getCommunityId();
            String newManagerId = selectedUser.getUser_id(); // Get the ID of the selected user

            // Check if the selected user is already in a different community
            if (selectedUserCommunityId != -1 && selectedUserCommunityId != communityId) {
                showAlert("Error", "Selected user is already assigned to another community.", Alert.AlertType.ERROR);
                return;
            }

            Message message = new Message(1, LocalDateTime.now(), "update community manager", new Object[]{communityId, newManagerId});
            try {
                SimpleClient.getClient("", 0).sendToServer(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Error", "Please select a user and a community.", Alert.AlertType.ERROR);
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

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
