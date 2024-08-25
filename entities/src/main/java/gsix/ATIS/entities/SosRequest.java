package gsix.ATIS.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name="sos_requests")
public class SosRequest implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "requester_id")
    private String requester_id;
    @Column(name = "first_name")
    private String first_name;

    @Column(name = "last_name")
    private String last_name;

    @Column(name = "location")
    private String location;

    @Column(name = "time")
    private LocalDateTime time;

    public SosRequest() {

    }

    public SosRequest(String requester_id, String first_name, String last_name, String location, LocalDateTime time) {
        this.requester_id = requester_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.location = location;
        this.time = time;
    }

    public SosRequest(String requester_id, String first_name, String last_name, String location) {
        this.requester_id = requester_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.location = location;
    }
    public String getRequester_id() {
        return requester_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getLocation() {
        return location;
    }

    public LocalDateTime getTime() {
        return time;
    }

/*public void setId(int id) {
        this.id = id;
    }*/

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "SosRequest{" +
                "requester_id='" + requester_id + '\'' +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", location='" + location + '\'' +
                ", time=" + time +
                '}';
    }
}
