package id.ac.ui.cs.advprog.papikosbe.service;

import id.ac.ui.cs.advprog.papikosbe.enums.NotificationType;
import id.ac.ui.cs.advprog.papikosbe.model.Notification;

import java.util.List;
import java.util.UUID;

public interface NotificationService {
    Notification createNotification(UUID userId, String title, String message, NotificationType type);
    List<Notification> getNotificationsForUser(UUID userId);
    void markAsRead(UUID notificationId);
    void deleteNotification(UUID notificationId);
}