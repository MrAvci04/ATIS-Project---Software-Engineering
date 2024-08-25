package gsix.ATIS.client;

import gsix.ATIS.entities.Message;

public class ErrorEvent {
    private Message message;

    public Message getMessage() {
        return message;
    }

    public ErrorEvent(Message message) {
        this.message = message;
    }
}
