package id.ac.ui.cs.advprog.papikosbe.model;

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

    public Notification(UUID id, UUID userId, String title, String message,
                        LocalDateTime createdAt, NotificationType type, boolean isRead) {
    }

    public UUID getId() {
        return null;
    }

    public UUID getUserId() {
        return null;
    }

    public String getTitle() {
        return null;
    }

    public String getMessage() {
        return null;
    }

    public LocalDateTime getCreatedAt() {
        return null;
    }

    public NotificationType getType() {
        return null;
    }

    public boolean isRead() {
        return false;
    }

    public void setRead(boolean read) {
    }

    public void setTitle(String title) {
    }

    public void setMessage(String message) {
    }
}
