package co.vivo.chatservice.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.vivo.chatservice.dto.Media;
import co.vivo.chatservice.dto.UserDto;
import co.vivo.chatservice.model.MessageEntity;
import co.vivo.chatservice.model.UserEntity;
import co.vivo.chatservice.repository.UserRepository;
import co.vivo.chatservice.service.AuthService;
import co.vivo.chatservice.service.ChatGroupService;
import co.vivo.chatservice.service.MessageService;
import co.vivo.chatservice.wrapper.ChatMessage;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/chats")
public class ChatController {

    Logger logger = LoggerFactory.getLogger(ChatController.class);


    @Inject
    AuthService authService;

    @Inject
    MessageService messageService;

    @Inject
    UserRepository userRepository;

    @Inject
    ChatGroupService chatGroupService;

    @GET
    @Path("/{userId}/contact")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getContacts(@PathParam("userId") String userId, @QueryParam("target") String target, @HeaderParam("Authorization") String token) {
        UserEntity userEntity = authService.verifyToken(token);
        if (userEntity == null || !userEntity.getUserId().equalsIgnoreCase(userId)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Authentication failed").build();
        }
        List<UserEntity> contacts = userRepository.findContactsForUser(userId);
        List<UserDto> user= contacts.stream().filter(f->f.getUserId().equalsIgnoreCase(target)).map(m->{
            return new UserDto(m.getFirstName(),m.getMiddleName(), m.getLastName(), m.getUserId());
        }).collect(Collectors.toList());
        return Response.ok(user).build();
    }

    // Endpoint to get all contacts a user has interacted with
    @GET
    @Path("/{userId}/contacts")
    public Response getAllContacts(@PathParam("userId") String userId, @HeaderParam("Authorization") String token) {
        UserEntity userEntity = authService.verifyToken(token);
        if (userEntity == null || !userEntity.getUserId().equalsIgnoreCase(userId)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Authentication failed").build();
        }
        List<UserEntity> userEntityList = userRepository.findAllContactsForUser(userId);
        List<UserDto> contacts = userEntityList.stream().filter(f->!f.getUserId().equalsIgnoreCase(userId)).map(m->{
            return new UserDto(m.getFirstName(),m.getMiddleName(), m.getLastName(), m.getUserId());
        }).collect(Collectors.toList());
        return Response.ok(contacts).build();
    }

    // Endpoint to get all messages between two users with pagination
    @GET
    @Path("/messages/{userId}")
    public Response getMessagesBetweenUsers(
            @PathParam("userId") String userId,
            @QueryParam("target") String target,
            @QueryParam(value ="page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size, @HeaderParam("Authorization") String token) {
        UserEntity userEntity = authService.verifyToken(token);
        if (userEntity == null || !userEntity.getUserId().equalsIgnoreCase(userId)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Authentication failed").build();
        }
        logger.info("Fetching messages between {} and {}", userId,target);

        List<MessageEntity> messages = messageService.getMessagesBetweenUsers(userId, target, page, size);
        logger.info("Fetched messages between {} and {} , messages={}", userId,target, messages);
        // Mapping each MessageEntity to ChatMessage
        List<ChatMessage> chatMessages = messages.stream().map(message -> {
            Media media = message.getMedia()!=null? new Media(message.getMedia().getId(), message.getMedia().getMediaUrl(), message.getMedia().getCreatedAt()):null;
            // Create and return ChatMessage with relevant fields
            return new ChatMessage(message.getId(),message.getReceiver(), message.getSender(),message.getContent(),media , message.getTimestamp()  );
        }).collect(Collectors.toList());
        logger.info("Returning messages between {} and {} , messages={}", userId,target, chatMessages);
        return Response.ok(messages).build();
    }

    // Endpoint to get all messages in a group with pagination
    @GET
    @Path("/group/{groupId}/messages")
    public Response getGroupMessages(
            @QueryParam("userId") String userId,
            @PathParam("groupId") Long groupId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size, @HeaderParam("Authorization") String token) {
        UserEntity userEntity = authService.verifyToken(token);
        if (userEntity == null || !userEntity.getUserId().equalsIgnoreCase(userId)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Authentication failed").build();
        }
        logger.info("Fetching messages between user: {} and group {}", userId, groupId);
        List<MessageEntity> messages = messageService.getMessagesInGroup(groupId, page, size);
        logger.info("Fetched messages between user: {} and group: {} , messages={}", userId,groupId, messages);
        // Mapping each MessageEntity to ChatMessage
        List<ChatMessage> chatMessages = messages.stream().map(message -> {
            Media media = message.getMedia()!=null? new Media(message.getMedia().getId(), message.getMedia().getMediaUrl(), message.getMedia().getCreatedAt()):null;
            return new ChatMessage(message.getId(),message.getReceiver(), message.getSender(),  message.getContent(), message.getGroupId(),media, message.getTimestamp()  );
        }).collect(Collectors.toList());
        logger.info("Returning messages between user: {} and group: {} , messages={}", userId,groupId, chatMessages);
        return Response.ok(chatMessages).build();
    }
}
