package co.vivo.chatservice.service;

import co.vivo.chatservice.model.MessageEntity;
import co.vivo.chatservice.model.UserEntity;
import co.vivo.chatservice.repository.MessageStatusRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service to handle message status updates like delivered and read acknowledgments.
 * Author: Asad Ali
 */
@ApplicationScoped
public class MessageStatusService {

    private static final Logger logger = LoggerFactory.getLogger(MessageStatusService.class);

    @Inject
    private MessageStatusRepository messageStatusRepository;

    /**
     * Marks a message as delivered for a specific user.
     */
    public void markAsDelivered(MessageEntity message, UserEntity user) {
        messageStatusRepository.updateDeliveredStatus(message, user, true);
        logger.info("Message {} marked as delivered for user {}", message.getId(), user.getUserId());
    }

    /**
     * Marks a message as read for a specific user.
     */
    public void markAsRead(MessageEntity message, UserEntity user) {
        messageStatusRepository.updateReadStatus(message, user, true);
        logger.info("Message {} marked as read for user {}", message.getId(), user.getUserId());
    }

    /**
     * Marks a message as sent for a specific user.
     */
    public void markAsSent(MessageEntity message, UserEntity user) {
        messageStatusRepository.updateSentStatus(message, user, true);
        logger.info("Message {} marked as sent for user {}", message.getId(), user.getUserId());
    }
}

