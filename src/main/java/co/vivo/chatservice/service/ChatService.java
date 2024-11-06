package co.vivo.chatservice.service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.vivo.chatservice.enums.Acknowledgment;
import co.vivo.chatservice.model.MessageEntity;
import co.vivo.chatservice.model.UserEntity;
import co.vivo.chatservice.repository.MessageRepository;
import co.vivo.chatservice.wrapper.ChatMessage;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.Session;

/**
 * Handles chat operations including direct and group messaging.
 * Author: Asad Ali
 */
@ApplicationScoped
public class ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    @Inject
    private MessageRepository messageRepository;

    @Inject
    private ChatGroupService chatGroupService;

    @Inject
    private MessageService messageService;

    @Inject
    private UserService userService;

    /**
     * Handles sending a direct message between users.
     */
    public void handleDirectMessage( ChatMessage chatMessage, Session session, ConcurrentHashMap<String, Session> sessions, MessageEntity messageEntity, UserEntity user) {
            Acknowledgment acknowledgment = messageService.sendMessage(chatMessage, session, sessions);
            if(Acknowledgment.SENT.equals(acknowledgment)) {
                messageService.markAsSent(messageEntity, user);
                messageService.sendSentAcknowledgmentBackToSender(chatMessage, session, messageEntity);
            }

    }

    /**
     * Handles sending a message to a group.
     */
    public void handleGroupMessage(ChatMessage chatMessage, Session session, ConcurrentHashMap<String, Session> sessions, MessageEntity messageEntity, UserEntity user) {
        List<UserEntity> groupUsers = chatGroupService.getGroupUsersbyGroupId(chatMessage.getGroupId(), chatMessage.getSender());
        if (groupUsers.stream().map(UserEntity::getUserId).anyMatch(id -> id.equals(chatMessage.getSender()))) {
            groupUsers.forEach(groupUser -> {
                if (!groupUser.getUserId().equalsIgnoreCase(chatMessage.getSender())) {
                    chatMessage.setRecipient(groupUser.getUserId());
                    Acknowledgment acknowledgment =messageService.sendMessage(chatMessage, session, sessions);
                    if(Acknowledgment.SENT.equals(acknowledgment)) {
                        messageService.markAsSent(messageEntity, user);
                        logger.info("Group Message {} Sent",chatMessage);
                    }
                }
            });
        } else {
            throw new RuntimeException("User not part of the group");
        }
    }

    /**
     * Broadcasts a message to all connected users except the sender.
     */
    public void broadcastMessage(ChatMessage chatMessage, Session session, ConcurrentHashMap<String, Session> sessions) {
        String messageToBroadcast = messageService.serializeMessage(chatMessage);
        sessions.values().forEach(s -> {
            if (!s.equals(session)) {
                s.getAsyncRemote().sendText(messageToBroadcast);
            }
        });
    }

    /**
     * Notifies users when a new user joins.
     */
    public void notifyUserJoined(String userId, Session session, ConcurrentHashMap<String, Session> sessions) {
        sessions.values().forEach(s -> s.getAsyncRemote().sendText("User " + userId + " has joined the chat"));
    }

    /**
     * Notifies users when a user leaves the chat.
     */
    public void notifyUserLeft(String userId, ConcurrentHashMap<String, Session> sessions) {
        sessions.values().forEach(s -> s.getAsyncRemote().sendText("User " + userId + " has left the chat"));
    }

    /**
     * Marks a message as delivered to a user.
     */
    public void markMessageAsDelivered(MessageEntity message, UserEntity user) {
        messageService.markAsDelivered(message, user);
    }

    /**
     * Marks a message as read by a user.
     */
    public void markMessageAsRead(MessageEntity message, UserEntity user) {
        messageService.markAsRead(message, user);
    }

    /**
     * Marks a message as sent by a user.
     */
    public void markMessageAsSent(MessageEntity message, UserEntity user) {
        messageService.markAsSent(message, user);
    }

    public UserEntity getUserBySession(Session session, ConcurrentHashMap<String, Session> sessions) {
        return sessions.entrySet().stream()
                .filter(entry -> entry.getValue().equals(session))
                .map(entry -> entry.getKey())
                .findFirst()
                .map(userId -> userService.getUserByUserId(userId))
                .orElse(null);
    }

}
