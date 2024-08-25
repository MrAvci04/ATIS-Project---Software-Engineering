package gsix.ATIS.client.common;

import gsix.ATIS.entities.Message;

import java.io.Serializable;

public class MessageEvent implements Serializable {
    private Message message;

    public Message getMessage() {
        return message;
    }

    public MessageEvent(Message message) {
        this.message = message;
    }
}
