package id.ac.ui.cs.advprog.papikosbe.service;

import id.ac.ui.cs.advprog.papikosbe.model.TopUp;
import id.ac.ui.cs.advprog.papikosbe.factory.TopUpFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class TopUpServiceImpl implements TopUpService {

    private final Map<UUID, TopUp> topUpRepository = new HashMap<>();
    private final TopUpFactory topUpFactory;

    public TopUpServiceImpl(TopUpFactory topUpFactory) {
        this.topUpFactory = topUpFactory;
    }

    public TopUp createTopUp(UUID userId, BigDecimal amount) {
        TopUp topUp = topUpFactory.createTopUp(userId, amount);
        return create(topUp);
    }

    @Override
    public TopUp create(TopUp topUp) {
        topUpRepository.put(topUp.getId(), topUp);
        return topUp;
    }

    @Override
    public List<TopUp> findAll() {
        return new ArrayList<>(topUpRepository.values());
    }

    @Override
    public TopUp findById(UUID id) {
        return topUpRepository.get(id);
    }

    @Override
    public List<TopUp> findByUserId(UUID userId) {
        return topUpRepository.values().stream()
                .filter(payment -> payment.getUserId().equals(userId))
                .toList();
    }

    @Override
    public List<TopUp> findByDate(LocalDateTime date) {
        return topUpRepository.values().stream()
                .filter(payment -> payment.getTimestamp().toLocalDate().equals(date.toLocalDate()))
                .toList();
    }
}
