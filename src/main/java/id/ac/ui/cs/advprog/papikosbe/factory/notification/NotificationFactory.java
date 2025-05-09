package id.ac.ui.cs.advprog.papikosbe.factory.notification;

import id.ac.ui.cs.advprog.papikosbe.model.notification.Notification;

public interface NotificationFactory {
    Notification createNotification(String title, String message);
}