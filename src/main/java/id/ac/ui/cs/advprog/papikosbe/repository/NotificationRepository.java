package id.ac.ui.cs.advprog.papikosbe.repository;

import id.ac.ui.cs.advprog.papikosbe.model.Notification;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class NotificationRepository {

    private final Map<UUID, Notification> storage = new HashMap<>();

    public Notification save(Notification notification) {
        storage.put(notification.getId(), notification);
        return notification;
    }

    public Notification findById(UUID id) {
        return storage.get(id);
    }

    public List<Notification> findAll() {
        return new ArrayList<>(storage.values());
    }

    public void deleteById(UUID id) {
        storage.remove(id);
    }

    public List<Notification> findByUserId(UUID userId) {
        return storage.values()
                .stream()
                .filter(n -> n.getUserId().equals(userId))
                .collect(Collectors.toList());
    }
}