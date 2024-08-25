package gsix.ATIS.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "communities")
public class Community implements Serializable {  // Implement Serializable

    private static final long serialVersionUID = 1L;  // Add serialVersionUID

    @Id
    private int community_id;

    @Column(name = "community_name")
    private String community_name;

    @Column(name = "manager_id")
    private String manager_id;

    public Community() {
    }

    public int getCommunity_id() {
        return community_id;
    }

    public void setCommunity_id(int community_id) {
        this.community_id = community_id;
    }

    public String getCommunity_name() {
        return community_name;
    }

    public void setCommunity_name(String community_name) {
        this.community_name = community_name;
    }

    public String getManager_id() {
        return manager_id;
    }

    public void setManager_id(String manager_id) {
        this.manager_id = manager_id;
    }

    @Override
    public String toString() {
        return "Community{" +
                "community_id=" + community_id +
                ", community_name='" + community_name + '\'' +
                ", manager_id='" + manager_id + '\'' +
                '}';
    }
}
