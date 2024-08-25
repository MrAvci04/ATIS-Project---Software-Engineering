package gsix.ATIS.client;

import gsix.ATIS.entities.Message;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class CommunityMessageController {//By Ayal

    public static void send(String senderID, String receiverID, String message){
        //store the message in the CommunityMessage table(using mySQL)
        //we need to use the server to access the data base
        String messageString=senderID+","+ receiverID+","+message;
        Message message2 = new Message(1, LocalDateTime.now(), "send message", messageString);
        System.out.println("Community Message Controller : sending the message");
        try {
            SimpleClient.getClient("",0).sendToServer(message2);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    public static void sendToUser(String managerID, String requesterID, String msg) {
//        String msgString = managerID+","+ requesterID+","+msg;
//        Message message = new Message(1, LocalDateTime.now(), "send decline message to user", msgString);
//        //System.out.println("Community Message Controller : sending msg to user");
//        try {
//            SimpleClient.getClient("",0).sendToServer(message);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public static void getMessageBox(String receiverID){
        //get all the messages that the receiverID has received.(using mySQL)
        //need to use the server to access the data base

        Message message = new Message(1, LocalDateTime.now(), "get received messages", receiverID);
        System.out.println("CommunityMessageController: getMessageBox");
        try {
            SimpleClient.getClient("",0).sendToServer(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
