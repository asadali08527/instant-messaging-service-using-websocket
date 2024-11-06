package co.vivo.chatservice.controller;


import co.vivo.chatservice.dto.GroupDto;
import co.vivo.chatservice.model.ChatGroupEntity;
import co.vivo.chatservice.model.UserEntity;
import co.vivo.chatservice.repository.ChatGroupRepository;
import co.vivo.chatservice.service.AuthService;
import co.vivo.chatservice.service.ChatGroupService;
import co.vivo.chatservice.wrapper.Group;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Collectors;

@Path("/groups")
public class ChatGroupController {

    @Inject
    private ChatGroupRepository chatGroupRepository;

    @Inject
    private AuthService authService; // To verify user tokens

    @Inject
    private ChatGroupService chatGroupService;

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createGroup(GroupDto chatGroup, @HeaderParam("Authorization") String token) {
        UserEntity adminUser = authService.verifyToken(token);
        if (adminUser == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Authentication failed").build();
        }
        chatGroupService.createChatGroup(chatGroup,adminUser);
        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllGroups( @HeaderParam("Authorization") String token) {
        UserEntity groupUser = authService.verifyToken(token);
        List<Group> groups = chatGroupService.getAllGroups(groupUser);
        return Response.ok(groups).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{groupId}")
    public Response getGroup(@PathParam("groupId") Long groupId, @HeaderParam("Authorization") String token) {
        UserEntity groupUser = authService.verifyToken(token);
        if (groupUser == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Authentication failed").build();
        }
        Group group =chatGroupService.getGroupByGroupId(groupId, groupUser);
        if(group!=null)
            return Response.ok(group).build();
        else
            return Response.ok("Either Request Group doesn't exist or You do not have permission").build();
    }

    // Endpoint to get all groups a user has joined
    @GET
    @Path("/user/{userId}")
    public Response getUserGroups(@PathParam("userId") String userId, @HeaderParam("Authorization") String token) {
        UserEntity userEntity = authService.verifyToken(token);
        if (userEntity == null || !userEntity.getUserId().equalsIgnoreCase(userId)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Authentication failed").build();
        }
        List<ChatGroupEntity> groups = chatGroupService.getGroupsByUserId(userEntity.getId());
        // Mapping each ChatGroupEntity to GroupRequestDto
        List<GroupDto> groupDtos = groups.stream().map(group -> {
            // Extract userIds from the group
            List<String> userIds = group.getUsers().stream()
                    .map(UserEntity::getUserId) // get the userId of each UserEntity
                    .collect(Collectors.toList());

            // Create and return GroupRequestDto with groupId, groupName, and userIds
            return new GroupDto(group.getId(), group.getGroupName(), userIds);
        }).collect(Collectors.toList());

        return Response.ok(groupDtos).build();
    }
}
