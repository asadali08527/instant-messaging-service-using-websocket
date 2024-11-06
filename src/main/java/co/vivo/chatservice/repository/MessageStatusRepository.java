package co.vivo.chatservice.repository;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.vivo.chatservice.model.MessageEntity;
import co.vivo.chatservice.model.MessageStatusEntity;
import co.vivo.chatservice.model.UserEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

/**
 * Repository to handle message status-related database operations.
 * Author: Asad Ali
 */
@ApplicationScoped
public class MessageStatusRepository {

    private static final Logger logger = LoggerFactory.getLogger(MessageStatusRepository.class);

    @PersistenceContext
    private EntityManager em;

    /**
     * Saves a new message status entity.
     */
    @Transactional
    public void saveMessageStatus(MessageStatusEntity status) {
        logger.info("Saving message status: {}", status);
        em.persist(status);
        em.flush();
    }

    /**
     * Finds message statuses by message entity.
     */
    @Transactional
    public List<MessageStatusEntity> findStatusesByMessage(MessageEntity message) {
        return em.createQuery("SELECT ms FROM MessageStatusEntity ms WHERE ms.message = :message", MessageStatusEntity.class)
                .setParameter("message", message)
                .getResultList();
    }

    /**
     * Finds a specific message status for a message and user.
     */
    @Transactional
    public MessageStatusEntity findStatus(MessageEntity message, UserEntity user) {
        return em.createQuery("SELECT ms FROM MessageStatusEntity ms WHERE ms.message = :message AND ms.user = :user", MessageStatusEntity.class)
                .setParameter("message", message)
                .setParameter("user", user)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    /**
     * Updates the delivered status of a message for a user.
     */
    @Transactional
    public void updateDeliveredStatus(MessageEntity message, UserEntity user, boolean delivered) {
        MessageStatusEntity status = findStatus(message, user);
        if (status != null) {
            status.setDelivered(delivered);
            status.setTimestampDelivered(delivered ? java.time.LocalDateTime.now() : null);
            em.merge(status);
        } else {
            status = new MessageStatusEntity(message, user);
            status.setDelivered(delivered);
            status.setTimestampDelivered(delivered ? java.time.LocalDateTime.now() : null);
            saveMessageStatus(status);
        }
    }

    /**
     * Updates the read status of a message for a user.
     */
    @Transactional
    public void updateReadStatus(MessageEntity message, UserEntity user, boolean read) {
        MessageStatusEntity status = findStatus(message, user);
        if (status != null) {
            status.setRead(read);
            status.setTimestampRead(read ? java.time.LocalDateTime.now() : null);
            em.merge(status);
        } else {
            status = new MessageStatusEntity(message, user);
            status.setRead(read);
            status.setTimestampRead(read ? java.time.LocalDateTime.now() : null);
            saveMessageStatus(status);
        }
    }

    /**
     * Updates the sent status of a message for a user.
     */
    @Transactional
    public void updateSentStatus(MessageEntity message, UserEntity user, boolean sent) {
        MessageStatusEntity status = findStatus(message, user);
        if (status != null) {
            status.setSent(sent);
            status.setTimestampSent(sent ? java.time.LocalDateTime.now() : null);
            em.merge(status);
        } else {
            status = new MessageStatusEntity(message, user);
            status.setSent(sent);
            status.setTimestampSent(sent ? java.time.LocalDateTime.now() : null);
            saveMessageStatus(status);
        }
    }
}

