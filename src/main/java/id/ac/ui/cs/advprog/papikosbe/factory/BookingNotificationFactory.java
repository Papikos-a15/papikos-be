package id.ac.ui.cs.advprog.papikosbe.factory;

import id.ac.ui.cs.advprog.papikosbe.enums.NotificationType;
import id.ac.ui.cs.advprog.papikosbe.model.Notification;

import java.time.LocalDateTime;
import java.util.UUID;

public class BookingNotificationFactory implements NotificationFactory {

    private final UUID userId;

    public BookingNotificationFactory(UUID userId) {
        this.userId = userId;
    }

    @Override
    public Notification createNotification(String title, String message) {
        return new Notification(UUID.randomUUID(), userId, title, message, LocalDateTime.now(), NotificationType.BOOKING, false);
    }
}
