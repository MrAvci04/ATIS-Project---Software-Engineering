package gsix.ATIS.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name="users")
public class User implements Serializable {
    @Id
    @Column(name = "user_id")
    private String user_id;
    @Column(name = "user_type")
    private String user_type;  // CHANGE THIS TO ENUM
    @Column(name = "first_name")
    private String first_name;
    @Column(name = "last_name")
    private String last_name;
    @Column(name = "user_name")
    private String user_name;
    @Column(name = "password")
    private String password;
    @Column(name = "community_id")
    private int community_id;
    @OneToMany(mappedBy = "requester")
    private List<Task> tasks;
    @Column(name= "logged_in")
    private int logged_in;

    public int getLogged_in() {
        return logged_in;
    }

    public void setLogged_in(int logged_in) {
        this.logged_in = logged_in;
    }

    public User() {
    }

    public User(String user_id, String user_type, String first_name, String last_name, String user_name, String password, int community_id,int logged_in) {
        this.user_id = user_id;
        this.user_type = user_type;
        this.first_name = first_name;
        this.last_name = last_name;
        this.user_name = user_name;
        this.password = password;
        this.community_id = community_id;
        this.logged_in=logged_in;
    }

    public User( String user_name, String password,String user_type) {
        this.user_type = user_type;
        this.user_name = user_name;
        this.password = password;
    }

    public String getUser_id() {
        return user_id;
    }


    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getCommunityId() {
        return community_id;
    }

    public void setCommunity(int community_id) {
        this.community_id = community_id;
    }


    @Override
    public String toString() {
        return "User{" +
                "user_id=" + user_id +
                ", user_type='" + user_type + '\'' +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", user_name='" + user_name + '\'' +
                ", password='" + password + '\'' +
                ", community_id='" + community_id + '\'' +
                '}';
    }
}
