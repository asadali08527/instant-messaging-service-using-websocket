package co.vivo.chatservice.model;

import co.vivo.chatservice.enums.ReadReceipt;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing user preferences for the chat system.
 * Author: Asad Ali
 */
@Entity
@Table(name = "user_preferences", indexes = {
        @Index(columnList = "user_id", unique = true)
})
public class UserPreferences {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    @Column(nullable = false)
    private ReadReceipt readReceipt;

    @Column(nullable = false)
    private boolean muteNotifications;

    @Column(nullable = false)
    private boolean hideSeenStatus;

    private LocalDateTime updatedAt;

    public UserPreferences() {
    }

    public UserPreferences(UserEntity user, ReadReceipt readReceipt, boolean muteNotifications, boolean hideSeenStatus) {
        this.user = user;
        this.readReceipt = readReceipt;
        this.muteNotifications = muteNotifications;
        this.hideSeenStatus = hideSeenStatus;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public ReadReceipt getReadReceipt() {
        return readReceipt;
    }

    public void setReadReceipt(ReadReceipt readReceipt) {
        this.readReceipt = readReceipt;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isMuteNotifications() {
        return muteNotifications;
    }

    public void setMuteNotifications(boolean muteNotifications) {
        this.muteNotifications = muteNotifications;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isHideSeenStatus() {
        return hideSeenStatus;
    }

    public void setHideSeenStatus(boolean hideSeenStatus) {
        this.hideSeenStatus = hideSeenStatus;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
