package id.ac.ui.cs.advprog.papikosbe.service.transaction;

import id.ac.ui.cs.advprog.papikosbe.model.TopUp;
import id.ac.ui.cs.advprog.papikosbe.factory.TopUpFactory;
import id.ac.ui.cs.advprog.papikosbe.repository.TopUpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class TopUpServiceImpl implements TopUpService {

    @Autowired
    private TopUpRepository topUpRepository;

    @Autowired
    private final TopUpFactory topUpFactory;

    public TopUpServiceImpl(TopUpFactory topUpFactory) {
        this.topUpFactory = topUpFactory;
    }

    @Override
    public TopUp createTopUp(UUID userId, BigDecimal amount) {
        TopUp topUp = TopUpFactory.createTopUp(userId, amount);

        return create(topUp);
    }

    @Override
    public TopUp create(TopUp topUp) {
        topUpRepository.save(topUp);
        return topUp;
    }

    @Override
    public List<TopUp> findAll() {
        return topUpRepository.findAll();
    }

    @Override
    public TopUp findById(UUID id) {
        return topUpRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("TopUp with ID " + id + " not found"));
    }

    @Override
    public List<TopUp> findByUserId(UUID userId) {
        return topUpRepository.findByUserId(userId);
    }

    @Override
    public List<TopUp> findByDate(LocalDateTime date) {
        return topUpRepository.findByDate(date);
    }
}
