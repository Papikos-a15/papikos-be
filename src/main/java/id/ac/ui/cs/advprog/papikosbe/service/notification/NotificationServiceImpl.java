package id.ac.ui.cs.advprog.papikosbe.service.notification;

import id.ac.ui.cs.advprog.papikosbe.enums.NotificationType;
import id.ac.ui.cs.advprog.papikosbe.model.notification.Notification;
import id.ac.ui.cs.advprog.papikosbe.repository.notification.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public Notification createNotification(UUID userId, String title, String message, NotificationType type) {
        Notification notification = Notification.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .title(title)
                .message(message)
                .createdAt(LocalDateTime.now())
                .type(type)
                .isRead(false)
                .build();
        return notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getNotificationsForUser(UUID userId) {
        return notificationRepository.findByUserId(userId);
    }

    @Override
    public void markAsRead(UUID notificationId) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);

        notificationOpt.ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }

    @Override
    public void deleteNotification(UUID notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}
