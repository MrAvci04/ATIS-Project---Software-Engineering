package gsix.ATIS.client.ocsf;

import gsix.ATIS.entities.Message;

public class NewSubscriberEvent {
    private Message message;

    public Message getMessage() {
        return message;
    }

    public NewSubscriberEvent(Message message) {
        this.message = message;
    }
}
