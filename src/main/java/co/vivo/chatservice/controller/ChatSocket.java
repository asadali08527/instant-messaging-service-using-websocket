package co.vivo.chatservice.controller;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.vivo.chatservice.enums.Acknowledgment;
import co.vivo.chatservice.model.MessageEntity;
import co.vivo.chatservice.model.UserEntity;
import co.vivo.chatservice.repository.MessageRepository;
import co.vivo.chatservice.repository.UserRepository;
import co.vivo.chatservice.service.AuthService;
import co.vivo.chatservice.service.ChatService;
import co.vivo.chatservice.service.MessageService;
import co.vivo.chatservice.service.MessageStatusService;
import co.vivo.chatservice.wrapper.ChatMessage;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

/**
 * WebSocket chat handler that manages user sessions and messaging between users and groups.
 * Author: Asad Ali
 */
@ServerEndpoint("/chat/{userId}")
@ApplicationScoped
public class ChatSocket {

    private static final Logger logger = LoggerFactory.getLogger(ChatSocket.class);

    @Inject
    private AuthService authService;

    @Inject
    private ChatService chatService;

    @Inject
    private MessageRepository messageRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private MessageService messageService;

    @Inject
    private MessageStatusService messageStatusService;


    private final ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper(); // For JSON processing

    /**
     * Called when a new WebSocket connection is opened.
     * Authenticates the user based on token or device ID.
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        CompletableFuture.runAsync(() -> {
            try {
                UserEntity user = authService.authenticateUser(session, userId);
                if (user != null) {
                    sessions.put(userId, session);
                    chatService.notifyUserJoined(userId, session, sessions);
                } else {
                    closeSession(session, "Authentication failed");
                }
            } catch (Exception e) {
                logger.error("Error during WebSocket connection setup: {}", e.getMessage());
            }
        });
    }

    /**
     * Handles incoming messages, differentiating between direct and group messages.
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        CompletableFuture.runAsync(() -> {
            try {
                ChatMessage chatMessage = objectMapper.readValue(message, ChatMessage.class);
                UserEntity user = chatService.getUserBySession(session, sessions);
                if (chatMessage.isAcknowledgment()) {
                    handleAcknowledgment(chatMessage, user);
                } else {
                    processMessage(chatMessage, session, user);
                }
            } catch (IOException e) {
                logger.error("Failed to parse message: {}", e.getMessage());
            }
        });
    }

    private void processMessage(ChatMessage chatMessage, Session session, UserEntity user) {
        chatMessage.setSender(user.getUserId());
        if (chatMessage.getRecipient() != null) {
            MessageEntity messageEntity = messageService.saveUserMessage(chatMessage.getSender(), chatMessage.getRecipient(), chatMessage.getContent(), chatMessage.getMedia().getMediaUrl(), chatMessage.getMessageId());
            chatService.handleDirectMessage(chatMessage, session, sessions, messageEntity, user);
        } else if (chatMessage.getGroupId() != null) {
            MessageEntity messageEntity = messageService.saveGroupMessage(chatMessage.getSender(), chatMessage.getGroupId(), chatMessage.getContent(), chatMessage.getMedia().getMediaUrl(), chatMessage.getMessageId());
            chatService.handleGroupMessage(chatMessage, session, sessions, messageEntity, user);
            messageService.sendSentAcknowledgmentBackToSender(chatMessage, session, messageEntity);
        } else {
            chatService.broadcastMessage(chatMessage, session, sessions);
        }
    }
    /**
     * Handles acknowledgment messages (delivered/read receipts).
     */
    private void handleAcknowledgment(ChatMessage chatMessage, UserEntity user) {
        try {
            Long messageId = chatMessage.getId();
            if (messageId != null && user != null) {
                MessageEntity message = messageService.getMessageById(messageId);
                if (message != null) {
                    if (Acknowledgment.DELIVERED.name().equalsIgnoreCase(chatMessage.getStatus())) {
                        messageStatusService.markAsDelivered(message, user);
                    } else if (Acknowledgment.READ.name().equalsIgnoreCase(chatMessage.getStatus())) {
                        messageStatusService.markAsRead(message, user);
                    }
                } else {
                    logger.warn("Message with ID {} not found for acknowledgment", messageId);
                }
            } else {
                logger.warn("Invalid acknowledgment message: {}", chatMessage);
            }
        } catch (Exception e) {
            logger.error("Error handling acknowledgment for message {}: {}", chatMessage.getId(), e.getMessage());
        }
    }
    /**
     * Called when a WebSocket connection is closed.
     */
    @OnClose
    public void onClose(Session session, @PathParam("userId") String userId) {
        sessions.remove(userId);
        chatService.notifyUserLeft(userId, sessions);
    }

    /**
     * Closes the session with a given reason.
     */
    private void closeSession(Session session, String reason) {
        try {
            session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, reason));
        } catch (IOException e) {
            logger.error("Error while closing session: {}", e.getMessage());
        }
    }
}
