package co.vivo.chatservice.service;

import co.vivo.chatservice.dto.UserPreferencesDto;
import co.vivo.chatservice.enums.ReadReceipt;
import co.vivo.chatservice.model.UserPreferences;
import co.vivo.chatservice.model.UserEntity;
import co.vivo.chatservice.repository.UserPreferencesRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.Optional;

@ApplicationScoped
public class UserPreferencesService {

    @Inject
    private UserPreferencesRepository userPreferencesRepository;

    /**
     * Get the user preferences by userId.
     */
    public Optional<UserPreferences> getUserPreferences(Long userId) {
        return userPreferencesRepository.findByUserId(userId);
    }

    /**
     * Update the user preferences.
     */
    @Transactional
    public void updateUserPreferences(Long userId, UserPreferencesDto newPreferences) {
        Optional<UserPreferences> existingPreferences = userPreferencesRepository.findByUserId(userId);

        existingPreferences.ifPresent(preferences -> {
            preferences.setReadReceipt(ReadReceipt.valueOf(newPreferences.getReadReceipt()));
            preferences.setMuteNotifications(newPreferences.isMuteNotifications());
            preferences.setHideSeenStatus(newPreferences.isHideSeenStatus());
            preferences.setUpdatedAt(java.time.LocalDateTime.now());
            userPreferencesRepository.updateUserPreferences(preferences);
        });
    }

    /**
     * Create new user preferences (if not exist).
     */
    @Transactional
    public void createUserPreferences(UserEntity user, UserPreferencesDto preferences) {
        UserPreferences userPreferences = new UserPreferences(
                user,
                ReadReceipt.valueOf(preferences.getReadReceipt()),
                preferences.isMuteNotifications(),
                preferences.isHideSeenStatus()
        );
        userPreferencesRepository.saveUserPreferences(userPreferences);
    }
}
