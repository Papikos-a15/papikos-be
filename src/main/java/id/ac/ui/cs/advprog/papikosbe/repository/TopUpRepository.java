package id.ac.ui.cs.advprog.papikosbe.repository;

import id.ac.ui.cs.advprog.papikosbe.model.TopUp;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TopUpRepository {
    private final List<TopUp> topUpData = new ArrayList<>();

    public TopUp create(TopUp topUp) {
        if (topUp.getId() == null) {
            topUp.setId(UUID.randomUUID());
        }
        topUpData.add(topUp);
        return topUp;
    }

    public TopUp save(TopUp topUp) {
        // Save = update or create
        Optional<TopUp> existing = findById(topUp.getId());
        existing.ifPresent(topUpData::remove); // Kalau ada, remove dulu
        topUpData.add(topUp);
        return topUp;
    }

    public Iterator<TopUp> findAll() {
        return topUpData.iterator();
    }

    public Optional<TopUp> findById(UUID topUpId) {
        for (TopUp topUp : topUpData) {
            if (topUp.getId().equals(topUpId)) {
                return Optional.of(topUp);
            }
        }
        return Optional.empty();
    }

    public List<TopUp> findByUserId(UUID userId) {
        List<TopUp> result = new ArrayList<>();
        for (TopUp topUp : topUpData) {
            if (topUp.getUserId().equals(userId)) {
                result.add(topUp);
            }
        }
        return result;
    }

    public void delete(UUID topUpId) {
        findById(topUpId).ifPresent(topUpData::remove);
    }
}