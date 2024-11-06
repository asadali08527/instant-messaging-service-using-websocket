package co.vivo.chatservice.repository;

import java.util.Optional;

import co.vivo.chatservice.model.UserPreferences;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

/**
 * Repository for managing user preferences.
 * Author: Asad Ali
 */
@ApplicationScoped
public class UserPreferencesRepository {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void saveUserPreferences(UserPreferences preferences) {
        em.persist(preferences);
    }

    @Transactional
    public void updateUserPreferences(UserPreferences preferences) {
        em.merge(preferences);
    }

    @Transactional
    public Optional<UserPreferences> findByUserId(Long userId) {
        return em.createQuery("SELECT up FROM UserPreferences up WHERE up.user.id = :userId", UserPreferences.class)
                .setParameter("userId", userId)
                .getResultStream()
                .findFirst();
    }

    @Transactional
    public void deleteUserPreferences(UserPreferences preferences) {
        em.remove(em.contains(preferences) ? preferences : em.merge(preferences));
    }
}
