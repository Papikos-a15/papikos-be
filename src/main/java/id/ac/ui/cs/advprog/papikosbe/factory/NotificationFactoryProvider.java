package id.ac.ui.cs.advprog.papikosbe.factory;

import id.ac.ui.cs.advprog.papikosbe.enums.NotificationType;

import java.util.UUID;

public class NotificationFactoryProvider {

    public static NotificationFactory getFactory(NotificationType type, UUID userId) {
        return switch (type) {
            case BOOKING -> new BookingNotificationFactory(userId);
            case PAYMENT -> new PaymentNotificationFactory(userId);
            case SYSTEM -> new SystemNotificationFactory(userId);
            case OTHER -> new OtherNotificationFactory(userId);
        };
    }
}