package id.ac.ui.cs.advprog.papikosbe.observer;

import id.ac.ui.cs.advprog.papikosbe.model.notification.Notification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationPublisherTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private NotificationPublisher notificationPublisher;

    @Test
    void testPublish_CallsConvertAndSend() {
        Notification notification = new Notification();
        notification.setUserId(UUID.randomUUID());
        notification.setMessage("Payment refunded");

        notificationPublisher.publish(notification);

        verify(messagingTemplate, times(1)).convertAndSend(
                eq("/queue/notifications/" + notification.getUserId()),
                eq(notification)
        );
    }
}
