/**
 * Sample Skeleton for 'OpenRequest.fxml' Controller Class
 */

package gsix.ATIS.client.user;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import gsix.ATIS.client.SimpleClient;
import gsix.ATIS.client.common.GuiCommon;
import gsix.ATIS.client.common.SosBoundary;
import gsix.ATIS.entities.Message;
import gsix.ATIS.entities.Task;
import gsix.ATIS.entities.TaskStatus;
import gsix.ATIS.entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class OpenRequestBoundary {

    private Stage stage;
    private User requester;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="back_Btn"
    private Button back_Btn; // Value injected by FXMLLoader

    @FXML // fx:id="requested_op_TxtFld"
    private TextField requested_op_TxtFld; // Value injected by FXMLLoader

    @FXML // fx:id="requster_id_Lbl"
    private Label requster_id_Lbl; // Value injected by FXMLLoader

    @FXML // fx:id="save_Btn"
    private Button save_Btn; // Value injected by FXMLLoader

    @FXML // fx:id="status_Lbl"
    private Label status_Lbl; // Value injected by FXMLLoader

    @FXML // fx:id="task_id_Lbl"
    private Label task_id_Lbl; // Value injected by FXMLLoader

    @FXML // fx:id="time_Lbl"
    private Label time_Lbl; // Value injected by FXMLLoader

    @FXML // fx:id="volunteer_id_Lbl"
    private Label volunteer_id_Lbl; // Value injected by FXMLLoader

    @FXML
    private ComboBox<String> selectedOP_cmbBx;

    private final String[] pickOperation = {"Groceries", "Medications","Car Repair","Babysitting","Uber","other"};


    //  SOS
    @FXML
    private Button SoS_Btn;
    //  SOS

    //  SOS
    @FXML
    void OpenSosCall(ActionEvent event) {
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow(); // first time stage takes value
        GuiCommon guiCommon = GuiCommon.getInstance();
        SosBoundary sosBoundary = (SosBoundary) guiCommon.displayNextScreen("SosWindow.fxml",
                "SoS Call", stage, false);  // Example for opening new screen
        sosBoundary.setRequester(requester);
    }
    //  SOS

    @FXML
    void backBtnClicked(MouseEvent event) {
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        //GuiCommon.getInstance().displayNextScreen("UserHomePage.fxml", "Community User Home Page", stage, true);
        UserHomePageBoundary userHomePage = (UserHomePageBoundary) GuiCommon.getInstance().displayNextScreen("UserHomePage.fxml",
                "Community User Home Page", stage, true);  // Example for opening new screen
        userHomePage.setLoggedInUser(requester);
    }

    @FXML
    void saveBtnClicked(MouseEvent event) {
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        try {
            String requestedOp = selectedOP_cmbBx.getValue();
            if(requestedOp.equals("other")){
                requestedOp = this.requested_op_TxtFld.getText();
            }
            Task task = new Task(this.requester.getUser_id(),requestedOp,TaskStatus.Request,requester.getCommunityId());

            Message message = new Message(1, LocalDateTime.now(), "open request", task);
            //System.out.println("before send to server GetAllTasks command");
            SimpleClient.getClient("",0).sendToServer(message);
            //System.out.println("after send to server GetAllTasks command");
            // Show the alert to confirm that the request has been sent to the manager
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Request Sent");
            alert.setHeaderText(null);
            alert.setContentText("Your request has been sent to the manager for approval.");
            alert.showAndWait(); // Wait for the user to close the alert


            //GuiCommon.getInstance().displayNextScreen("UserHomePage.fxml", "Community User Home Page", stage, true);
            UserHomePageBoundary userHomePage = (UserHomePageBoundary) GuiCommon.getInstance().displayNextScreen("UserHomePage.fxml",
                    "Community User Home Page", stage, true);  // Example for opening new screen
            userHomePage.setLoggedInUser(requester);
            /// make (UserHome page) or (Server) send the manager message to approve the task

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setRequesterInfo(User user) {
        this.requester=user;
        this.requster_id_Lbl.setText(requester.getUser_id());
    }
    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert back_Btn != null : "fx:id=\"back_Btn\" was not injected: check your FXML file 'OpenRequest.fxml'.";
        assert requested_op_TxtFld != null : "fx:id=\"requested_op_TxtFld\" was not injected: check your FXML file 'OpenRequest.fxml'.";
        assert requster_id_Lbl != null : "fx:id=\"requster_id_Lbl\" was not injected: check your FXML file 'OpenRequest.fxml'.";
        assert save_Btn != null : "fx:id=\"save_Btn\" was not injected: check your FXML file 'OpenRequest.fxml'.";
        assert status_Lbl != null : "fx:id=\"status_Lbl\" was not injected: check your FXML file 'OpenRequest.fxml'.";
        assert task_id_Lbl != null : "fx:id=\"task_id_Lbl\" was not injected: check your FXML file 'OpenRequest.fxml'.";
        assert time_Lbl != null : "fx:id=\"time_Lbl\" was not injected: check your FXML file 'OpenRequest.fxml'.";
        assert volunteer_id_Lbl != null : "fx:id=\"volunteer_id_Lbl\" was not injected: check your FXML file 'OpenRequest.fxml'.";

        this.status_Lbl.setText("Request");
        requested_op_TxtFld.setDisable(true);

        selectedOP_cmbBx.getItems().addAll(pickOperation);
        selectedOP_cmbBx.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null && newValue.equals("other")) {
                requested_op_TxtFld.setDisable(false);
            } else {
                requested_op_TxtFld.setDisable(true);
            }
        });
    }

}
