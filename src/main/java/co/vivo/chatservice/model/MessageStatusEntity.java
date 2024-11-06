package co.vivo.chatservice.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing the delivery and read status of a message for a specific user.
 * Author: Asad Ali
 */
@Entity
@Table(name = "message_statuses",
        indexes = {
                @Index(columnList = "message_id"),
                @Index(columnList = "user_id")
        })
public class MessageStatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private MessageEntity message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private Boolean delivered = false;

    @Column(nullable = false)
    private Boolean read = false;

    @Column(nullable = false)
    private Boolean sent = false;

    private LocalDateTime timestampDelivered;

    private LocalDateTime timestampRead;

    private LocalDateTime timestampSent;

    // Constructors
    public MessageStatusEntity() {
    }

    public MessageStatusEntity(MessageEntity message, UserEntity user) {
        this.message = message;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MessageEntity getMessage() {
        return message;
    }

    public void setMessage(MessageEntity message) {
        this.message = message;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Boolean getDelivered() {
        return delivered;
    }

    public void setDelivered(Boolean delivered) {
        this.delivered = delivered;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public LocalDateTime getTimestampDelivered() {
        return timestampDelivered;
    }

    public void setTimestampDelivered(LocalDateTime timestampDelivered) {
        this.timestampDelivered = timestampDelivered;
    }

    public LocalDateTime getTimestampRead() {
        return timestampRead;
    }

    public void setTimestampRead(LocalDateTime timestampRead) {
        this.timestampRead = timestampRead;
    }

    // Override toString for better logging
    @Override
    public String toString() {
        return "MessageStatusEntity{" +
                "id=" + id +
                ", messageId=" + (message != null ? message.getId() : null) +
                ", userId=" + (user != null ? user.getUserId() : null) +
                ", delivered=" + delivered +
                ", read=" + read +
                ", timestampDelivered=" + timestampDelivered +
                ", timestampRead=" + timestampRead +
                '}';
    }

    public Boolean getSent() {
        return sent;
    }

    public void setSent(Boolean sent) {
        this.sent = sent;
    }

    public LocalDateTime getTimestampSent() {
        return timestampSent;
    }

    public void setTimestampSent(LocalDateTime timestampSent) {
        this.timestampSent = timestampSent;
    }
}

