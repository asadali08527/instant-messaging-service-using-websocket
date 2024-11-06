package co.vivo.chatservice.repository;

import co.vivo.chatservice.model.MessageEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
/**
 * Repository to handle message-related database operations.
 * Author: Asad Ali
 */
@ApplicationScoped
public class MessageRepository {

    private static final Logger logger = LoggerFactory.getLogger(MessageRepository.class);

    @PersistenceContext
    private EntityManager em;

    /**
     * Saves a message entity to the database.
     */
    @Transactional
    public MessageEntity saveMessage(MessageEntity message) {
        logger.info("Saving Message: {}", message);
        em.persist(message);
        em.flush();
        return message;
    }

    /**
     * Retrieves the message history, ordered by timestamp.
     */
    @Transactional
    public List<MessageEntity> getMessageHistory(int limit) {
        return em.createQuery("SELECT m FROM MessageEntity m ORDER BY m.timestamp DESC", MessageEntity.class)
                .setMaxResults(limit)
                .getResultList();
    }

    /**
     * Updates a message with new content.
     */
    @Transactional
    public MessageEntity updateMessage(Long id, String newContent) {
        MessageEntity message = em.find(MessageEntity.class, id);
        if (message != null) {
            message.setContent(newContent);
            message.setTimestamp(LocalDateTime.now());
            return em.merge(message);
        }
        return null;
    }

    /**
     * Retrieves messages between two users with pagination.
     */
    @Transactional
    public List<MessageEntity> getMessagesBetweenUsers(String user1, String user2, int page, int size) {
        TypedQuery<MessageEntity> query = em.createQuery(
                "SELECT m FROM MessageEntity m LEFT JOIN FETCH m.media WHERE (m.sender = :user1 AND m.receiver = :user2) " +
                        "OR (m.sender = :user2 AND m.receiver = :user1) ORDER BY m.timestamp DESC", MessageEntity.class);
        return query.setParameter("user1", user1)
                .setParameter("user2", user2)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }
    /**
     * Retrieves group messages with pagination.
     */
    @Transactional
    public List<MessageEntity> getMessagesInGroup(Long groupId, int page, int size) {
        TypedQuery<MessageEntity> query = em.createQuery(
                "SELECT m FROM MessageEntity m LEFT JOIN FETCH m.media WHERE m.groupId = :groupId ORDER BY m.timestamp DESC",
                MessageEntity.class);
        return query.setParameter("groupId", groupId)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }
    /**
     * Retrieves messages between users in a specific time range.
     */
    @Transactional
    public List<MessageEntity> getMessagesBetweenUsersInTimeRange(String user1, String user2, LocalDateTime startTime, LocalDateTime endTime, int page, int size) {
        TypedQuery<MessageEntity> query = em.createQuery(
                "SELECT m FROM MessageEntity m WHERE ((m.sender = :user1 AND m.receiver = :user2) OR (m.sender = :user2 AND m.receiver = :user1)) AND m.timestamp BETWEEN :startTime AND :endTime ORDER BY m.timestamp DESC",
                MessageEntity.class);
        return query.setParameter("user1", user1)
                .setParameter("user2", user2)
                .setParameter("startTime", startTime)
                .setParameter("endTime", endTime)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    /**
     * Finds a message by its ID.
     */
    @Transactional
    public MessageEntity findById(Long messageId) {
        return em.find(MessageEntity.class, messageId);
    }
}

