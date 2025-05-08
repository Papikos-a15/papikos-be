package id.ac.ui.cs.advprog.papikosbe.repository;

import id.ac.ui.cs.advprog.papikosbe.model.TopUp;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TopUpRepository {
    private final List<TopUp> topUpData = new ArrayList<>();

    public TopUp save(TopUp topUp) {
        // Save = update or create
        Optional<TopUp> existing = findById(topUp.getId());
        existing.ifPresent(topUpData::remove); // Kalau ada, remove dulu
        topUpData.add(topUp);
        return topUp;
    }

    public List<TopUp> findAll() {
        return new ArrayList<>(topUpData);
    }

    public Optional<TopUp> findById(UUID topUpId) {
        return topUpData.stream()
                .filter(topUp -> topUp.getId().equals(topUpId))
                .findFirst();
    }

    public List<TopUp> findByUserId(UUID userId) {
        return topUpData.stream()
                .filter(topUp -> topUp.getUserId().equals(userId))
                .toList();
    }

    public List<TopUp> findByDate(LocalDateTime date) {
        return topUpData.stream()
                .filter(payment -> payment.getTimestamp().toLocalDate().equals(date.toLocalDate()))
                .toList();
    }

    public void delete(UUID topUpId) {
        findById(topUpId).ifPresent(topUpData::remove);
    }
}