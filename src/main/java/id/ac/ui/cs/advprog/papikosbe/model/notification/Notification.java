package id.ac.ui.cs.advprog.papikosbe.model.notification;

import java.time.LocalDateTime;
import java.util.UUID;

import id.ac.ui.cs.advprog.papikosbe.enums.NotificationType;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class Notification {

    private UUID id;
    private UUID userId;
    private String title;
    private String message;
    private LocalDateTime createdAt;
    private NotificationType type;
    private boolean isRead;

    private Notification(Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.title = builder.title;
        this.message = builder.message;
        this.createdAt = builder.createdAt != null ? builder.createdAt : LocalDateTime.now();
        this.type = builder.type;
        this.isRead = builder.isRead;
    }

    public static class Builder {
        private UUID id;
        private UUID userId;
        private String title;
        private String message;
        private LocalDateTime createdAt;
        private NotificationType type;
        private boolean isRead;

        public Builder(UUID id, UUID userId) {
            this.id = id;
            this.userId = userId;
        }

        public Builder setTitle(String title) {
            if (title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("Title cannot be null or empty");
            }
            this.title = title;
            return this;
        }

        public Builder setMessage(String message) {
            if (message == null || message.trim().isEmpty()) {
                throw new IllegalArgumentException("Message cannot be null or empty");
            }
            this.message = message;
            return this;
        }

        public Builder setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder setType(NotificationType type) {
            this.type = type;
            return this;
        }

        public Builder setIsRead(boolean isRead) {
            this.isRead = isRead;
            return this;
        }

        public Notification build() {
            return new Notification(this);
        }
    }
}
