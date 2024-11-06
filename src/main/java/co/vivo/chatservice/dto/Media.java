package co.vivo.chatservice.dto;

import java.time.LocalDateTime;

public class Media {


    private Long id;


    private String mediaUrl;


    private LocalDateTime createdAt = LocalDateTime.now();

    public Media() {}

    public Media(Long id, String mediaUrl, LocalDateTime createdAt) {
        this.id = id;
        this.mediaUrl = mediaUrl;
        this.createdAt = createdAt;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

