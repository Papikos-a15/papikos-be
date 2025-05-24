package id.ac.ui.cs.advprog.papikosbe.service.notification;

import id.ac.ui.cs.advprog.papikosbe.enums.NotificationType;
import id.ac.ui.cs.advprog.papikosbe.model.notification.Notification;
import id.ac.ui.cs.advprog.papikosbe.model.user.User;
import id.ac.ui.cs.advprog.papikosbe.repository.notification.NotificationRepository;
import id.ac.ui.cs.advprog.papikosbe.repository.user.UserRepository;
import id.ac.ui.cs.advprog.papikosbe.observer.NotificationPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationPublisher notificationPublisher;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   NotificationPublisher notificationPublisher,
                                   UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.notificationPublisher = notificationPublisher;
        this.userRepository = userRepository;
    }

    @Override
    @Async
    public CompletableFuture<Notification> createNotification(UUID userId, String title, String message, NotificationType type) {
        Notification notification = Notification.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .title(title)
                .message(message)
                .createdAt(LocalDateTime.now())
                .type(type)
                .isRead(false)
                .build();
        Notification saved = notificationRepository.save(notification);

        notificationPublisher.publish(saved);

        return CompletableFuture.completedFuture(saved);
    }

    @Override
    @Async
    public void createNotificationForAllUser(String title, String message, NotificationType type){
        List<User> users = userRepository.findAll();
        for (User user : users) {
            Notification notification = Notification.builder()
                    .id(UUID.randomUUID())
                    .userId(user.getId())
                    .title(title)
                    .message(message)
                    .createdAt(LocalDateTime.now())
                    .type(type)
                    .isRead(false)
                    .build();
            Notification saved = notificationRepository.save(notification);

            notificationPublisher.publish(saved);
        }
    }

    @Override
    public List<Notification> getNotificationsForUser(UUID userId) {
        return notificationRepository.findByUserId(userId);
    }

    @Override
    public Notification getNotificationById(UUID id) {
        return notificationRepository.findById(id).orElse(null);
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
