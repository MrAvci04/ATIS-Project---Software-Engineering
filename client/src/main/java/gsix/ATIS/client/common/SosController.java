package gsix.ATIS.client.common;

import gsix.ATIS.client.SimpleClient;
import gsix.ATIS.entities.Message;
import gsix.ATIS.entities.SosRequest;
import gsix.ATIS.entities.User;

import java.time.LocalDateTime;

public class SosController {

    public static void sendSosRequest(User requester){

        String location = LocationTracker.getLocation(); // this line retrieve user location by getLocation method at LocationTracker class, which relys on windows api

        SosRequest sosRequest = new SosRequest(requester.getUser_id(), requester.getFirst_name(), requester.getLast_name(),location, LocalDateTime.now());

        System.out.println("*********INSIDE sendSosRequest*********");
        Message message = new Message(1, LocalDateTime.now(), "open SoS request", sosRequest);
        try {
            SimpleClient.getClient("", 0).sendToServer(message);
            System.out.println("*********INSIDE sendSosRequest: Message sent to server*********");
        }catch (Exception e){

        }
        System.out.println(location);
    }
}
