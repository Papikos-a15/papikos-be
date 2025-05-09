package id.ac.ui.cs.advprog.papikosbe.model.notification;

import java.time.LocalDateTime;
import java.util.UUID;

import id.ac.ui.cs.advprog.papikosbe.enums.NotificationType;

public class Notification {

    private UUID id;
    private UUID userId;
    private String title;
    private String message;
    private LocalDateTime createdAt;
    private NotificationType type;
    private boolean isRead;

    public Notification(UUID id, UUID userId, String title, String message, LocalDateTime createdAt, NotificationType type, boolean isRead) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }

        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be null or empty");
        }

        this.id = id;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.type = type;
        this.isRead = isRead;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public NotificationType getType() {
        return type;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        this.isRead = read;
    }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        this.title = title;
    }

    public void setMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be null or empty");
        }
        this.message = message;
    }
}
