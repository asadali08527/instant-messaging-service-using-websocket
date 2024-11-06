package co.vivo.chatservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import co.vivo.chatservice.converter.UserConverter;
import co.vivo.chatservice.dto.GroupDto;
import co.vivo.chatservice.model.ChatGroupEntity;
import co.vivo.chatservice.model.UserEntity;
import co.vivo.chatservice.repository.ChatGroupRepository;
import co.vivo.chatservice.wrapper.Group;
import co.vivo.chatservice.wrapper.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ChatGroupService {

    @Inject
    ChatGroupRepository chatGroupRepository;

    @Inject
    UserService userService;
    public void createChatGroup(GroupDto chatGroup, UserEntity adminUser) {
        ChatGroupEntity chatGroupEntity = new ChatGroupEntity();
        List<UserEntity> userEntityList = new ArrayList<>();
        chatGroupEntity.setGroupName(chatGroup.getGroupName());
        for(String userId: chatGroup.getUsers()){
            userEntityList.add(userService.getUserByUserId(userId));
        }
        chatGroupEntity.getUsers().addAll(userEntityList);
        // Set the admin user in the group (if needed)
        chatGroupEntity.getUsers().add(adminUser); // Add the admin to the group
        chatGroupRepository.save(chatGroupEntity);
    }

    public Group getGroupByGroupId(Long groupId, UserEntity groupUser) {
        ChatGroupEntity chatGroupEntity = chatGroupRepository.findById(groupId);
        Group group = new Group();
        List<User> users = new ArrayList<>();
        group.setGroupName(chatGroupEntity.getGroupName());
        boolean isValidRequester = false;
        for(UserEntity userEntity: chatGroupEntity.getUsers()){
            if(!isValidRequester)
                isValidRequester = userEntity.getUserId().equalsIgnoreCase(groupUser.getUserId());
            users.add(UserConverter.convert(userEntity));
        }
        if(isValidRequester) {
            group.getUsers().addAll(users);
            return group;
        }else{
            return null;
        }
    }

    @Transactional
    public List<UserEntity> getGroupUsersbyGroupId(Long groupId, String groupUser) {
        ChatGroupEntity chatGroupEntity = chatGroupRepository.findById(groupId);
        boolean isValidRequester = false;
        for(UserEntity userEntity: chatGroupEntity.getUsers()){
            if(!isValidRequester)
                isValidRequester = userEntity.getUserId().equalsIgnoreCase(groupUser);
        }
        if(isValidRequester) {
            return chatGroupEntity.getUsers();
        }else{
            return null;
        }
    }

    public List<Group> getAllGroups(UserEntity groupUser) {
        List<ChatGroupEntity> ChatGroupEntityList = chatGroupRepository.findGroupsByUserId(groupUser.getId());
        return ChatGroupEntityList.stream().map(chatGroupEntity->{
            Group group = new Group();
            List<User> users = new ArrayList<>();
            group.setGroupName(chatGroupEntity.getGroupName());
            for(UserEntity userEntity: chatGroupEntity.getUsers()){
                users.add(UserConverter.convert(userEntity));
            }
            group.getUsers().addAll(users);
            return group;
        }).collect(Collectors.toList());
    }

    public List<ChatGroupEntity> getGroupsByUserId(Long userId) {
        return chatGroupRepository.findGroupsByUserId(userId);
    }
}
