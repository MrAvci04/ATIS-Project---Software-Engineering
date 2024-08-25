/**
 * Sample Skeleton for 'SosWindow.fxml' Controller Class
 */

package gsix.ATIS.client.common;

import java.net.URL;
import java.util.ResourceBundle;

import gsix.ATIS.entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class SosBoundary {
    private User requester;

    private Stage stage=null;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="Cancel_Btn"
    private Button Cancel_Btn; // Value injected by FXMLLoader

    @FXML // fx:id="confirmSos_Btn"
    private Button confirmSos_Btn; // Value injected by FXMLLoader

    @FXML
    void Cancel(ActionEvent event) {
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    void Confirm(ActionEvent event) {
        SosController.sendSosRequest(requester);
        showSuccessMessage();
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }


    private void showSuccessMessage() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("SoS Call Message");
        alert.setHeaderText(null);
        alert.setContentText("Your SoS Call have been received successfully!");

        // Show the alert dialog
        alert.showAndWait();
    }

    public User getRequester() {
        return requester;
    }

    public void setRequester(User requester) {
        this.requester = requester;
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert Cancel_Btn != null : "fx:id=\"Cancel_Btn\" was not injected: check your FXML file 'SosWindow.fxml'.";
        assert confirmSos_Btn != null : "fx:id=\"confirmSos_Btn\" was not injected: check your FXML file 'SosWindow.fxml'.";

    }

}
