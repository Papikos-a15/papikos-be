package id.ac.ui.cs.advprog.papikosbe.service;

import id.ac.ui.cs.advprog.papikosbe.model.TopUp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TopUpService {
    TopUp create(TopUp topUp);
    List<TopUp> findAll();
    TopUp findById(UUID id);
    List<TopUp> findByUserId(UUID userId);
    List<TopUp> findByDate(LocalDateTime date);
}
