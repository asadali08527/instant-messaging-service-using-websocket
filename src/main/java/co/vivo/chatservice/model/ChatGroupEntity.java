package co.vivo.chatservice.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
/**
 * Entity representing a chat group.
 * Author: Asad Ali
 */
@Entity
@Table(name = "chat_groups")
public class ChatGroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String groupName;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "group_users",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<UserEntity> users = new ArrayList<>();

    public ChatGroupEntity() {
    }

    public ChatGroupEntity(String groupName, List<UserEntity> users) {
        this.groupName = groupName;
        this.users = users;
    }

    public ChatGroupEntity(Long id, String groupName, List<UserEntity> users) {
        this.id = id;
        this.groupName = groupName;
        this.users = users;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<UserEntity> getUsers() {
        return users;
    }

    public void setUsers(List<UserEntity> users) {
        this.users = users;
    }
}
