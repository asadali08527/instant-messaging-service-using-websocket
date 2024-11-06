package co.vivo.chatservice.service;

import co.vivo.chatservice.enums.Acknowledgment;
import co.vivo.chatservice.model.MediaEntity;
import co.vivo.chatservice.model.MessageEntity;
import co.vivo.chatservice.model.UserEntity;
import co.vivo.chatservice.repository.MessageRepository;
import co.vivo.chatservice.repository.MessageStatusRepository;
import co.vivo.chatservice.wrapper.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service to handle message sending and storage.
 * Author: Asad Ali
 */
@ApplicationScoped
public class MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Inject
    MessageRepository messageRepository;

    @Inject
    private MessageStatusRepository messageStatusRepository;


    public List<MessageEntity> getMessagesBetweenUsers(String user1, String user2, int page, int size) {
        return messageRepository.getMessagesBetweenUsers(user1, user2, page, size);
    }

    public List<MessageEntity> getMessagesInGroup(Long groupId, int page, int size) {
        return messageRepository.getMessagesInGroup(groupId, page, size);
    }

    public List<MessageEntity> getMessagesBetweenUsersInTimeRange(String user1, String user2, LocalDateTime startTime, LocalDateTime endTime, int page, int size) {
        return messageRepository.getMessagesBetweenUsersInTimeRange(user1, user2, startTime, endTime, page, size);
    }

    /**
     * Sends a message from one user to another.
     */
    public Acknowledgment sendMessage(ChatMessage chatMessage, Session senderSession, ConcurrentHashMap<String, Session> sessions) {
        Session recipientSession = sessions.get(chatMessage.getRecipient());
        if (recipientSession != null) {
            recipientSession.getAsyncRemote().sendText(serializeMessage(chatMessage));
            return Acknowledgment.SENT;
        } else {
            senderSession.getAsyncRemote().sendText("User " + chatMessage.getRecipient() + " is not connected.");
            return Acknowledgment.FAILED;
        }
    }

    public void sendSentAcknowledgmentBackToSender(ChatMessage chatMessage, Session session, MessageEntity messageEntity) {
        chatMessage.setId(messageEntity.getId());
        chatMessage.setStatus(Acknowledgment.SENT.name());
        chatMessage.setAcknowledgment(true);
        if(chatMessage.getGroupId()!=null) {
            //Update recipient as null while sending group back message acknowledgement
            chatMessage.setRecipient(null);
        }
        session.getAsyncRemote().sendText(serializeMessage(chatMessage));
    }
    public MessageEntity saveMessage(String senderId, String recipientId, Long groupId, String content, String mediaUrl, String messageId) {
        MessageEntity msgEntity = new MessageEntity();
        msgEntity.setSender(senderId);

        if (groupId != null) {
            msgEntity.setGroupId(groupId);
        } else {
            msgEntity.setReceiver(recipientId);
        }

        msgEntity.setContent(content);
        msgEntity.setMessageId(messageId);
        msgEntity.setTimestamp(LocalDateTime.now());

        // If there's a media URL, save it to MediaEntity
        if (mediaUrl != null && !mediaUrl.isEmpty()) {
            MediaEntity mediaEntity = new MediaEntity(mediaUrl, LocalDateTime.now());
            msgEntity.setMedia(mediaEntity);
        }

        msgEntity = messageRepository.saveMessage(msgEntity);
        logger.info("Message saved: {}", msgEntity);
        return msgEntity;
    }

    /**
     * Saves a direct message to the database.
     */
    public MessageEntity saveUserMessage(String senderId, String recipientId, String content, String mediaUrl, String messageId) {
        return saveMessage(senderId,recipientId, null, content,mediaUrl,messageId );
    }

    /**
     * Saves a group message to the database.
     */
    public MessageEntity saveGroupMessage(String senderId, Long groupId, String content, String mediaUrl, String messageId) {
        return saveMessage(senderId,null, groupId, content,mediaUrl,messageId );
    }

    /**
     * Serializes a ChatMessage object to a JSON string.
     */
    public String serializeMessage(ChatMessage message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            logger.error("Error serializing message: {}", e.getMessage());
            return "";
        }
    }

    /**
     * Marks a message as delivered for a specific user.
     */
    public void markAsDelivered(MessageEntity message, UserEntity user) {
        messageStatusRepository.updateDeliveredStatus(message, user, true);
        logger.info("Marked message {} as delivered for user {}", message.getId(), user.getUserId());
    }

    /**
     * Marks a message as read for a specific user.
     */
    public void markAsRead(MessageEntity message, UserEntity user) {
        messageStatusRepository.updateReadStatus(message, user, true);
        logger.info("Marked message {} as read for user {}", message.getId(), user.getUserId());
    }

    /**
     * Marks a message as sent for a specific user.
     */
    public void markAsSent(MessageEntity message, UserEntity user) {
        messageStatusRepository.updateSentStatus(message, user, true);
        logger.info("Marked message {} as sent for user {}", message.getId(), user.getUserId());
    }

    public MessageEntity getMessageById(Long messageId) {
        return messageRepository.findById(messageId);
    }
}
