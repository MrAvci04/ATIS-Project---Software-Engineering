package gsix.ATIS.client;

import java.io.IOException;

import javafx.fxml.FXML;

public class SecondaryController {

    @FXML
    public void switchToPrimary() throws IOException {
        SimpleChatClient.setRoot("primary");
    }
}