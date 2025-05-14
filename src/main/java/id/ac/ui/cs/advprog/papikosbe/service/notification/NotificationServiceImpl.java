package id.ac.ui.cs.advprog.papikosbe.service.notification;

import id.ac.ui.cs.advprog.papikosbe.enums.NotificationType;
import id.ac.ui.cs.advprog.papikosbe.factory.notification.NotificationFactory;
import id.ac.ui.cs.advprog.papikosbe.factory.notification.NotificationFactoryProvider;
import id.ac.ui.cs.advprog.papikosbe.model.notification.Notification;
import id.ac.ui.cs.advprog.papikosbe.repository.notification.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        NotificationFactory factory = NotificationFactoryProvider.getFactory(type, userId);
        Notification notification = factory.createNotification(title, message);
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