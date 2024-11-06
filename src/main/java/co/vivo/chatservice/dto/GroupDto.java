package co.vivo.chatservice.dto;


import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupDto {


    private Long id;

    private String groupName;


    private List<String> users = new ArrayList<>();

    public GroupDto() {
    }

    public GroupDto(String groupName, List<String> users) {
        this.groupName = groupName;
        this.users = users;
    }

    public GroupDto(Long id, String groupName, List<String> users) {
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

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }
}

