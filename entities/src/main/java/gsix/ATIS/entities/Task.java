package gsix.ATIS.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javax.persistence.*;

@Entity
@Table(name="tasks")
public class Task implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int task_id;
    @Column(name = "requester_id")
    private String requester_id;
    @Column(name = "volunteer_id")
    private String volunteer_id;
    @Column(name = "requested_operation")
    private String requested_operation;

    @Column(name = "time")
    private LocalDateTime time;
    @Column(name = "status")
    private String status; // CHANGE THIS TO ENUM
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User requester;

    @Column(name="community_id")
    private int community_id;

    public Task() {
    }

    public Task(int task_id, String attachedMessage) {
        this.task_id = task_id;
        this.volunteer_id = attachedMessage;
    }

    public Task(int task_id, String volunteer_id, String requested_operation) {
        this.task_id = task_id;
        this.volunteer_id = volunteer_id;
        this.requested_operation = requested_operation;
    }

    public Task(String requester_id, String requested_operation, TaskStatus taskStatus, int community_id) {
        this.requester_id = requester_id;
        this.requested_operation = requested_operation;
        this.time = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        this.community_id = community_id;
        if (taskStatus == TaskStatus.Request){
            this.status = "Request";
        } else if (taskStatus == TaskStatus.Pending) {
            this.status = "Pending";
        }else if(taskStatus == TaskStatus.Done){
            this.status = "Done";
        }
        else if(taskStatus == TaskStatus.Declined){
            this.status = "Declined";
        }
        else if(taskStatus == TaskStatus.inProcess){
            this.status = "in process";
        }
    }

    public Task(String requester_id, String volunteer_id, String requested_operation, LocalDateTime time, TaskStatus taskStatus) {

        this.requester_id = requester_id;
        this.volunteer_id = volunteer_id;
        this.requested_operation = requested_operation;
        this.time = time;
        if (taskStatus == TaskStatus.Request){
            this.status = "Request";
        } else if (taskStatus == TaskStatus.Pending) {
            this.status = "Pending";
        }else if(taskStatus == TaskStatus.Done){
            this.status = "Done";
        }else if(taskStatus == TaskStatus.Declined){
            this.status = "Declined";
        }
        else if(taskStatus == TaskStatus.inProcess){
            this.status = "in process";
        }
    }

    public Task(String requester_id, String requested_operation, LocalDateTime time, String status, int community_id) {
        this.requester_id = requester_id;
        this.requested_operation = requested_operation;
        this.time = time;
        this.status = status;
        this.community_id = community_id;
    }
    public Task(String requester_id, String requested_operation, String status, int community_id) {
        this.requester_id = requester_id;
        this.requested_operation = requested_operation;
        this.time = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        this.status = status;
        this.community_id = community_id;
    }

    /*public Task(int task_id, String requester_id, String requested_operation, LocalDateTime time, TaskStatus status) {
        this.task_id = task_id;
        this.requester_id = requester_id;
        this.requested_operation = requested_operation;
        this.time = time;
        this.taskStatus = status;
        if (taskStatus == TaskStatus.Request){
            this.status = "Request";
        } else if (taskStatus == TaskStatus.Pending) {
            this.status = "Pending";
        }else if(taskStatus == TaskStatus.Done){
            this.status = "Done";
        }
        else if(taskStatus == TaskStatus.Declined){
            this.status = "Declined";
        }
        else if(taskStatus == TaskStatus.inProcess){
            this.status = "in process";
        }
    }*/

    public int getTask_id() {
        return task_id;
    }


    public String getRequester_id() {
        return requester_id;
    }

    public void setRequester_id(String requester_id) {
        this.requester_id = requester_id;
    }

    public String getVolunteer_id() {
        return volunteer_id;
    }

    public void setVolunteer_id(String volunteer_id) {
        this.volunteer_id = volunteer_id;
    }

    public String getRequested_operation() {
        return requested_operation;
    }

    public void setRequested_operation(String requested_operation) {
        this.requested_operation = requested_operation;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getStatus() {
        /*if (this.status.equals("Request") ){
            return TaskStatus.Request;
        } else if (this.status.equals("Pending") ){
            return TaskStatus.Pending;
        }else{
            return TaskStatus.Done;
        }*/
        return this.status;
    }

    /*public void setStatus(String taskStatus) {
        this.status=taskStatus;
    }*/
    public void setStatus(TaskStatus taskStatus) {
        if (taskStatus == TaskStatus.Request){
            this.status = "Request";
        } else if (taskStatus == TaskStatus.Pending) {
            this.status = "Pending";
        }else if(taskStatus == TaskStatus.Done){
            this.status = "Done";
        }
        else if(taskStatus == TaskStatus.Declined){
            this.status = "Declined";
        }
        else if(taskStatus == TaskStatus.inProcess){
            this.status = "in process";
        }
    }

    public int getCommunity_id() {
        return community_id;
    }

    @Override
    public String toString() {
        return "Task{" +
                "task_id=" + task_id +
                ", requester_id=" + requester_id +
                ", volunteer_id=" + volunteer_id +
                ", requested_operation='" + requested_operation + '\'' +
                ", time=" + time +
                ", status=" + status + // Use the String representation for consistency
                '}';
    }

}
