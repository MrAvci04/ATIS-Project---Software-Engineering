package gsix.ATIS.client;

import gsix.ATIS.client.common.GuiCommon;
import gsix.ATIS.client.common.MessageEvent;
import gsix.ATIS.client.user.UserHomePageBoundary;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * JavaFX App
 */
public class SimpleChatClient extends Application {

    private static Scene scene;
    private SimpleClient client;

    @Override
    public void start(Stage stage) throws IOException {
    	EventBus.getDefault().register(this);
    	/*client = SimpleClient.getClient();
    	client.openConnection();*/
        /*scene = new Scene(loadFXML("ClientForm"));
        stage.setScene(scene);
        stage.show();*/
        GuiCommon guiCommon = GuiCommon.getInstance();
        ClientFormController clientFormController = (ClientFormController) guiCommon.displayNextScreen("ClientForm.fxml",
                    "Client Connector", null, false);
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SimpleChatClient.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
    
    

    @Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub
    	EventBus.getDefault().unregister(this);
		super.stop();
	}


    @Subscribe
    public void onMessageEvent(MessageEvent message) {
        System.out.println("onMessageEvent");
        //client.handleMessageFromServer(message.getMessage());
    }


	public static void main(String[] args) {
        launch();
    }

}