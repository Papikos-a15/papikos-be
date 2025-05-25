package id.ac.ui.cs.advprog.papikosbe.observer;

import id.ac.ui.cs.advprog.papikosbe.model.notification.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public NotificationPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void publish(Notification notification) {
        messagingTemplate.convertAndSend("/queue/notifications/" + notification.getUserId(), notification);
    }
}
