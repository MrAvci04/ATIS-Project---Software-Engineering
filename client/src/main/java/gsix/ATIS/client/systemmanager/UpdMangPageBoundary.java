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

public class UpdMangPageBoundary {
    private User loggedInUser;
    @FXML
    private TextField userNameTextField;

    @FXML
    private TextField firstNameTextField;

    @FXML
    private TextField lastNameTextField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<Integer> communityIdComboBox;

    @FXML
    private Button updateManagerBtn;

    @FXML
    private Button backBtn;

    @FXML
    private ListView<User> userListView;

    @FXML
    void initialize() {
        EventBus.getDefault().register(this);
        loadCommunities();
        loadUsers();
        userListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Populate fields with selected user's details
                userNameTextField.setText(newValue.getUser_name() != null ? newValue.getUser_name() : "");
                firstNameTextField.setText(newValue.getFirst_name() != null ? newValue.getFirst_name() : "");
                lastNameTextField.setText(newValue.getLast_name() != null ? newValue.getLast_name() : "");
                passwordField.setText(newValue.getPassword() != null ? newValue.getPassword() : "");
            } else {
                // Clear fields if no user is selected
                userNameTextField.setText("");
                firstNameTextField.setText("");
                lastNameTextField.setText("");
                passwordField.setText("");
            }
        });
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
                ObservableList<User> observableUsers = FXCollections.observableArrayList(data);
                userListView.setItems(observableUsers);
            });
        }
    }





    @FXML
    void updateManager() {
        User selectedUser = userListView.getSelectionModel().getSelectedItem();
        Integer communityId = communityIdComboBox.getValue();

        if (selectedUser != null && communityId != null) {
            int selectedUserCommunityId = selectedUser.getCommunityId();
            String newManagerId = selectedUser.getUser_id();

            // Check if the user is already assigned to another community
            if (selectedUserCommunityId != -1 && selectedUserCommunityId != communityId) {
                showAlert("Error", "Selected user is already assigned to another community.", Alert.AlertType.ERROR);
                return;
            }

            // Prepare the updated user information
            Message message = new Message(1, LocalDateTime.now(), "update user",
                    new Object[]{
                            newManagerId,
                            (!userNameTextField.getText().isEmpty()) ? userNameTextField.getText() : selectedUser.getUser_name(),
                            (!firstNameTextField.getText().isEmpty()) ? firstNameTextField.getText() : selectedUser.getFirst_name(),
                            (!lastNameTextField.getText().isEmpty()) ? lastNameTextField.getText() : selectedUser.getLast_name(),
                            (!passwordField.getText().isEmpty()) ? passwordField.getText() : selectedUser.getPassword(),
                            selectedUser.getUser_type(), // Assuming user_type remains unchanged
                            communityId, // Set the new community_id
                            (selectedUser.getLogged_in() != -1) ? selectedUser.getLogged_in() : 0 // Default to 0 if logged_in is -1
                    }
            );

            // Send the message to the server
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
