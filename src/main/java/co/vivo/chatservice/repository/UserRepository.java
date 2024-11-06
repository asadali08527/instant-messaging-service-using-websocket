package co.vivo.chatservice.repository;

import co.vivo.chatservice.enums.UserType;
import co.vivo.chatservice.model.UserEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserRepository {

    @PersistenceContext
    EntityManager em;

    /**
     * Saves a new user entity.
     */
    @Transactional
    public UserEntity saveUser(UserEntity user) {
        em.persist(user);
        em.flush();
        return user;
    }

    /**
     * Updates an existing user entity.
     */
    @Transactional
    public UserEntity updateUser(UserEntity user) {
        return em.merge(user);
    }

    /**
     * Finds a user by email or mobile number.
     */
    @Transactional
    public Optional<UserEntity> findUserByEmailOrMobile(String emailOrMobile) {
        return em.createQuery("SELECT u FROM UserEntity u WHERE u.email = :emailOrMobile OR u.mobile = :emailOrMobile", UserEntity.class)
                .setParameter("emailOrMobile", emailOrMobile)
                .getResultStream()
                .findFirst();
    }

    /**
     * Finds a guest user by device ID.
     */
    @Transactional
    public Optional<UserEntity> findGuestByDeviceId(String deviceId) {
        return em.createQuery("SELECT u FROM UserEntity u WHERE u.deviceId = :deviceId", UserEntity.class)
                .setParameter("deviceId", deviceId)
                .getResultStream()
                .findFirst();
    }

    /**
     * Finds a user by their user ID and type.
     */
    @Transactional
    public Optional<UserEntity> findByIdAndUserType(String userId, UserType userType) {
        return em.createQuery("SELECT u FROM UserEntity u WHERE u.userId = :userId AND u.userType = :userType", UserEntity.class)
                .setParameter("userId", userId)
                .setParameter("userType", userType)
                .getResultStream()
                .findFirst();
    }

    /**
     * Finds a user by their user ID.
     */
    @Transactional
    public Optional<UserEntity> findByUserId(String userId) {
        return em.createQuery("SELECT u FROM UserEntity u WHERE u.userId = :userId", UserEntity.class)
                .setParameter("userId", userId)
                .getResultStream()
                .findFirst();
    }

    /**
     * Finds a user by their username.
     */
    @Transactional
    public Optional<UserEntity> findUserByUsername(String username) {
        return em.createQuery("SELECT u FROM UserEntity u WHERE u.username = :username", UserEntity.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst();
    }

    /**
     * Finds all contacts for a given user by user ID.
     */
    @Transactional
    public List<UserEntity> findAllContactsForUser(String userId) {
        return em.createQuery("SELECT DISTINCT u FROM MessageEntity m JOIN UserEntity u ON (m.sender = u.userId OR m.receiver = u.userId) WHERE m.sender = :userId OR m.receiver = :userId", UserEntity.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    /**
     * Finds contacts for a given user by user ID.
     */
    @Transactional
    public List<UserEntity> findContactsForUser(String userId) {
        return em.createQuery("SELECT DISTINCT u FROM MessageEntity m JOIN UserEntity u ON (m.sender = u.userId OR m.receiver = u.userId) WHERE m.sender = :userId OR m.receiver = :userId", UserEntity.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}
