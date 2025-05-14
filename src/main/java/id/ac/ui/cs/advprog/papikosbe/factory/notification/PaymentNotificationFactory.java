package id.ac.ui.cs.advprog.papikosbe.factory.notification;

import id.ac.ui.cs.advprog.papikosbe.enums.NotificationType;
import id.ac.ui.cs.advprog.papikosbe.model.notification.Notification;

import java.time.LocalDateTime;
import java.util.UUID;

public class PaymentNotificationFactory implements NotificationFactory {

    private final UUID userId;

    public PaymentNotificationFactory(UUID userId) {
        this.userId = userId;
    }

    @Override
    public Notification createNotification(String title, String message) {
        return new Notification(UUID.randomUUID(), userId, title, message, LocalDateTime.now(), NotificationType.PAYMENT, false);
    }
}
