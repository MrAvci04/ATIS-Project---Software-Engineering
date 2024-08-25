package gsix.ATIS.server;

import gsix.ATIS.entities.CommunityMessage;
import gsix.ATIS.entities.Message;
import gsix.ATIS.entities.Task;
import gsix.ATIS.entities.User;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/*
 * TO START THE THREAD :
 * NoVolunteerBGThread bgThread = new NoVolunteerBGThread();
 * Thread thread = new Thread(bgThread);
 * thread.start(); // Starts the background thread
 */
public class NoVolunteerBGThread implements Runnable{

    private Timer timer;
    private SimpleServer server;

    private final int THREAD_CYCLE_SECONDS= 60;

    NoVolunteerBGThread(SimpleServer server){
        this.server = server;
    }

    public void stopJob() {
        timer.cancel(); // Cancel the timer
    }

    @Override
    public void run() {

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Implement your background task here
                // Check if any task has exceeded 20 seconds with no volunteer
                // Notify system users if necessary
                // For demonstration, I'm printing a message to console
                System.out.println("Background thread is running...");
                getAllSystemOverDuePendingTasks();
                getAllSystemOverDueInProcessTasks();
            }
        }, 0, THREAD_CYCLE_SECONDS*1000); // Checks every 60*60 seconds (1 Hour), put 10*1000 to check every 10 seconds
    }

    private void getAllSystemOverDuePendingTasks() {
        // calling server method to retrieve pending tasks
        List<Task> allOverDuePendingTasks = server.getAllOverDuePendingTasks();
        // Iterate through pending tasks
        if (allOverDuePendingTasks != null) {
            for (Task task : allOverDuePendingTasks) {
                //if (checkNoVolunteerYet(task)) {
                    // If no volunteer yet for this task, notify community users
                    User taskRequester = getTaskRequester(task);
                    notifyCommunityUsers(taskRequester.getCommunityId(), task);
                //}
            }
        }
    }
    private void getAllSystemOverDueInProcessTasks() {
        // calling server method to retrieve pending tasks
        List<Task> allOverDueInProcessTasks = server.getAllOverDueInProcessTasks();
        // Iterate through pending tasks
        if (allOverDueInProcessTasks != null) {
            for (Task task : allOverDueInProcessTasks) {
                // If no volunteer yet for this task, notify community users
                //User taskVolunteerer = getTaskVolunteer(task);
                String managerID = server.getManagerID(task.getCommunity_id());
                String msgContent = "[Task Volunteering took Long Time]\nYou started volunteering for task: "+ task.getTask_id()+
                        "long time ago.\nDONT FORGET TO UPDATE MANAGER BY MESSAGE!!!";
                CommunityMessage lateNotification = new CommunityMessage(managerID,task.getVolunteer_id(),msgContent );
                server.saveMessage(lateNotification);
                notifyUserInProcessOverDue(lateNotification);
            }
        }
    }

    private User getTaskRequester(Task task) {
        return SimpleServer.getEntityById(User.class, Integer.parseInt(task.getRequester_id()));
    }
    private User getTaskVolunteer(Task task) {
        return SimpleServer.getEntityById(User.class, Integer.parseInt(task.getVolunteer_id()));
    }

    /**
     * we decided not to import all community of overdue task from server but rather
     * send to all clients only community_id with overDueTask then for each user we
     * check if he belongs to the same community then he'll get a pop-up notification.
     **/
    private List<User> getCommunityOfTaskRequester(User task_requester) {
        // Assuming server has a method to retrieve community users by community ID
        return SimpleServer.getAllCommunityMembers(task_requester.getCommunityId());
    }

    private void notifyCommunityUsers(int community_ID, Task overDueTask) {
        try {
            if (overDueTask != null) {
                /**
                 * overDueTask has no volunteer, so we used volunteer_id field to send the community_id
                 **/
                overDueTask.setVolunteer_id(community_ID + "");
                Message msg = new Message(1, LocalDateTime.now(), "notify overdue task",overDueTask);
                server.sendToAllClients(msg);
            }
        } catch (Exception e) {
            //throw new RuntimeException(e);
            System.out.println(e.getMessage());
        }
    }
    private void notifyUserInProcessOverDue(CommunityMessage lateNotification) {
        try {
            if (lateNotification != null) {
                /**
                 * overDueTask has no volunteer, so we used volunteer_id field to send the community_id
                 **/
                Message msg = new Message(1, LocalDateTime.now(), "notify InProcess task took so long",lateNotification);
                server.sendToAllClients(msg);
            }
        } catch (Exception e) {
            //throw new RuntimeException(e);
            System.out.println(e.getMessage());
        }
    }
}

