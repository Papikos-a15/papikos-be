package id.ac.ui.cs.advprog.papikosbe.service;

import id.ac.ui.cs.advprog.papikosbe.enums.NotificationType;
import id.ac.ui.cs.advprog.papikosbe.model.Notification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Override
    public Notification createNotification(UUID userId, String title, String message, NotificationType type) {
        return null;
    }

    @Override
    public List<Notification> getNotificationsForUser(UUID userId) {
        return null;
    }

    @Override
    public void markAsRead(UUID notificationId) {
    }

    @Override
    public void deleteNotification(UUID notificationId) {
    }
}
