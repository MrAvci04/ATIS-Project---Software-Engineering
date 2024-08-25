package gsix.ATIS.client.login;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import gsix.ATIS.client.SimpleClient;
import gsix.ATIS.client.common.DeviceIdentifier;
import gsix.ATIS.client.common.GuiCommon;
import gsix.ATIS.client.common.MessageEvent;
import gsix.ATIS.client.common.SosBoundary;
import gsix.ATIS.client.manager.ManagerHomePageBoundary;
import gsix.ATIS.client.user.UserHomePageBoundary;
import gsix.ATIS.client.systemmanager.SystemManagerHomePageBoundary;
import gsix.ATIS.entities.Message;
import gsix.ATIS.entities.User;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class LoginFrameBoundary implements Initializable {

	private Stage stage;

	@FXML
	private AnchorPane window;

	@FXML
	private TextField UsernameField;

	@FXML
	private PasswordField PasswordField;

	@FXML
	private Label msgArea;

	@FXML
	private Button SoS_Btn;

	@FXML
	private Button loginButton;

	@FXML
	private ComboBox<String> roleComboBox;

	private String username;
	private String password;
	private String selectedRole = "User";

	@FXML
	void OpenSosCall(ActionEvent event) {
		stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		GuiCommon guiCommon = GuiCommon.getInstance();
		SosBoundary sosBoundary = (SosBoundary) guiCommon.displayNextScreen("SosWindow.fxml",
				"SoS Call", stage, false);
		String macID = DeviceIdentifier.getMACAddress();
		System.out.println("SOS Call button was clicked. Your device MAC Address: " + macID);
		User unknown = new User(macID, "community user", "MAC Address", "MAC Address",
				"MAC Address", "MAC Address", 0, 0);
		sosBoundary.setRequester(unknown);
	}

	@FXML
	public void Login(ActionEvent event) throws IOException {
		stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.setOnCloseRequest(e -> {
			System.out.println("Stage is closing. Unregistering from EventBus.");
			if (EventBus.getDefault().isRegistered(this)) {
				EventBus.getDefault().unregister(this);
			}
		});

		username = UsernameField.getText();
		password = PasswordField.getText();
		if (username.trim().isEmpty()) {
			loginMsg("Username is a required field.");
			return;
		}
		if (password.trim().isEmpty()) {
			loginMsg("Password is a required field.");
			return;
		}

		User userDetails = new User(username, password, selectedRole);
		LoginController.loginUser(userDetails);
		System.out.println("Login request sent from LoginFrameBoundary.");
	}

	@Subscribe
	public void handleTasksEvent(MessageEvent event) {
		Message loginMessage = event.getMessage();

		switch (loginMessage.getMessage()) {
			case "login request: Done":
				User loggedInUser = (User) loginMessage.getData();
				Platform.runLater(() -> {
					if (selectedRole.equals("System Manager")) {
						if (loggedInUser.getUser_type().equals("system manager")) {
							GuiCommon guiCommon = GuiCommon.getInstance();
							SystemManagerHomePageBoundary systemManagerHomePageBoundary = (SystemManagerHomePageBoundary) guiCommon
									.displayNextScreen("SystemManagerHomePage.fxml", "System Manager Home Page", null, false);
							systemManagerHomePageBoundary.setLoggedInUser(loggedInUser);
							stage.close();
						} else {
							showAccessDeniedAlert();
						}
					} else if (selectedRole.equals("Manager")) {
						if (loggedInUser.getUser_type().equals("community user")) {
							showAccessDeniedAlert();
						} else {
							GuiCommon guiCommon = GuiCommon.getInstance();
							ManagerHomePageBoundary managerHomePageBoundary = (ManagerHomePageBoundary) guiCommon
									.displayNextScreen("ManagerHomePage.fxml", "Manager Home Page", null, false);
							managerHomePageBoundary.setLoggedInUser(loggedInUser);
							stage.close();
						}
					} else {
						GuiCommon guiCommon = GuiCommon.getInstance();
						if (loggedInUser.getUser_type().equals("community user")) {
							UserHomePageBoundary userHomePageBoundary = (UserHomePageBoundary) guiCommon
									.displayNextScreen("UserHomePage.fxml", "Community User Home Page", null, false);
							userHomePageBoundary.setLoggedInUser(loggedInUser);
							stage.close();
						} else {
							UserHomePageBoundary userHomePageBoundary = (UserHomePageBoundary) guiCommon
									.displayNextScreen("UserHomePage.fxml", "Community User Home Page", null, false);
							userHomePageBoundary.setLoggedInUser(loggedInUser);
							stage.close();
						}
					}
					EventBus.getDefault().unregister(this);
				});
				break;

			case "login request: Failed":
				String errMsg = (String) loginMessage.getData();
				Platform.runLater(() -> msgArea.setText(errMsg));
				break;

			case "login request: User already logged in":
				Platform.runLater(() -> {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setTitle("Log in Error");
					alert.setHeaderText("Already Logged In");
					alert.setContentText("You are already logged in, make sure to close prior windows to log in again");
					alert.showAndWait();
				});
				break;

			default:
				// Handle unknown messages if needed
				System.out.println("Unhandled message type: " + loginMessage.getMessage());
				break;
		}
	}


	private void showAccessDeniedAlert() {
		Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Access Denied");
			alert.setHeaderText("Permission Error");
			alert.setContentText("You do not have the correct permissions to access this area.");
			alert.showAndWait();
		});
		Message message = new Message(1, LocalDateTime.now(), "log out", new User(username, password, selectedRole));
		try {
			SimpleClient.getClient("", 0).sendToServer(message);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void loginMsg(String msg) {
		msgArea.setText(msg);
	}

	@FXML
	private void onRoleSelected() {
		selectedRole = roleComboBox.getValue();
		System.out.println("Selected Role: " + selectedRole);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ObservableList<String> roles = FXCollections.observableArrayList("User", "Manager", "System Manager");
		roleComboBox.setItems(roles);
		roleComboBox.setValue("User");

		roleComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			selectedRole = newValue;
			System.out.println("Selected role changed to: " + selectedRole);
		});

		if (!EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().register(this);
		}
	}

	public LoginFrameBoundary() {
		selectedRole = "User";
	}
}
