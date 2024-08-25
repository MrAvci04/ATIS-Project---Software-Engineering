package gsix.ATIS.client.systemmanager;

import gsix.ATIS.client.SimpleClient;
import gsix.ATIS.client.common.GuiCommon;
import gsix.ATIS.client.common.MessageEvent;
import gsix.ATIS.entities.Community;
import gsix.ATIS.entities.User;
import gsix.ATIS.entities.Message;
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

public class CrUsPageBoundary {

    private User loggedInUser;

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
    private Button backBtn;

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
            e.printStackTrace();
        }
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
        if ("add user to community: Done".equals(handledMessage.getMessage())) {
            Platform.runLater(() -> {
                loadUsers();  // Refresh the user list after adding a new user
            });
        }
    }

    @FXML
    void addUser() {
        String userId = userIdTF.getText();
        String firstName = firstNameTF.getText();
        String lastName = lastNameTF.getText();
        String userName = userNameTF.getText();
        String password = passwordTF.getText();
        String userType = userTypeComboBox.getValue();
        int communityId = communityIdComboBox.getValue();

        User newUser = new User(userId, userType, firstName, lastName, userName, password, communityId, 0);

        Message message = new Message(1, LocalDateTime.now(), "add user to community", newUser);
        try {
            SimpleClient.getClient("", 0).sendToServer(message);
        } catch (IOException e) {
            e.printStackTrace();
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
