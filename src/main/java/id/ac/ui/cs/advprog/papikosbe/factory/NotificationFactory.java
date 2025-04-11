package id.ac.ui.cs.advprog.papikosbe.factory;

import id.ac.ui.cs.advprog.papikosbe.model.Notification;

public interface NotificationFactory {
    Notification createNotification(String title, String message);
}