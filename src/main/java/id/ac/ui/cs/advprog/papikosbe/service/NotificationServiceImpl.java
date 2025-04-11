package id.ac.ui.cs.advprog.papikosbe.service;

import id.ac.ui.cs.advprog.papikosbe.enums.NotificationType;
import id.ac.ui.cs.advprog.papikosbe.model.Notification;
import id.ac.ui.cs.advprog.papikosbe.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public Notification createNotification(UUID userId, String title, String message, NotificationType type) {
        Notification notification = new Notification(
                UUID.randomUUID(),
                userId,
                title,
                message,
                LocalDateTime.now(),
                type,
                false // default unread
        );
        return notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getNotificationsForUser(UUID userId) {
        return notificationRepository.findAll().stream()
                .filter(n -> n.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public void markAsRead(UUID notificationId) {
        Optional<Notification> notificationOpt = Optional.ofNullable(notificationRepository.findById(notificationId));
        if (notificationOpt.isPresent()) {
            Notification notif = notificationOpt.get();
            notif.setRead(true);
            notificationRepository.save(notif);
        }
    }

    @Override
    public void deleteNotification(UUID notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}