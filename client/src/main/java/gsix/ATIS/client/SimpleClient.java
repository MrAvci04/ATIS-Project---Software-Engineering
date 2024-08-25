package gsix.ATIS.client;

import gsix.ATIS.client.common.MessageEvent;
import gsix.ATIS.client.ocsf.AbstractClient;
import gsix.ATIS.entities.Message;
import org.greenrobot.eventbus.EventBus;

public class SimpleClient extends AbstractClient {
	
	private static SimpleClient client = null;

	private SimpleClient(String host, int port) {
		super(host, port);
	}

	@Override
	protected void handleMessageFromServer(Object msg) {
		Message message = (Message) msg;
		System.out.println("I am in handle from server");
		MessageEvent messageEvent=new MessageEvent(message);
		EventBus.getDefault().post(messageEvent);

	}


	public static SimpleClient getClient(String server_ip,int server_port) {
		if (client == null) {
			client = new SimpleClient(server_ip, server_port);
		}
		return client;
	}

}
