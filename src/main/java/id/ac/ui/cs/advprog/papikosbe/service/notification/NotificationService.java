package id.ac.ui.cs.advprog.papikosbe.service.notification;

import id.ac.ui.cs.advprog.papikosbe.enums.NotificationType;
import id.ac.ui.cs.advprog.papikosbe.model.notification.Notification;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface NotificationService {
    CompletableFuture<Notification> createNotification(UUID userId, String title, String message, NotificationType type);
    void createNotificationForAllUser(String title, String message, NotificationType type);
    List<Notification> getNotificationsForUser(UUID userId);
    void markAsRead(UUID notificationId);
    void deleteNotification(UUID notificationId);
}