package gsix.ATIS.client.login;

import gsix.ATIS.entities.Message;

import javafx.event.ActionEvent;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LogInMemberEvent extends ActionEvent{
    LocalDateTime dateTimeEnd ;

    Message msg1 ;
    public  LogInMemberEvent (Message msg1){

        this.msg1 = msg1 ;
        this.dateTimeEnd = LocalDateTime.now();
        //this.dateTimeEnd = msg1.getTimeStamp();
    }
    public Message getMsg1() {
        return msg1;
    }

    public void setMsg1(Message msg1) {
        this.msg1 = msg1;
    }
}
