package id.ac.ui.cs.advprog.papikosbe.service.transaction;

import id.ac.ui.cs.advprog.papikosbe.model.transaction.TopUp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TopUpService {
    TopUp createTopUp(UUID userId, BigDecimal amount);
    TopUp create(TopUp topUp);
    List<TopUp> findAll();
    TopUp findById(UUID id);
    List<TopUp> findByUserId(UUID userId);
    List<TopUp> findByDate(LocalDateTime date);
}
