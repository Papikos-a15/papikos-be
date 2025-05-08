package id.ac.ui.cs.advprog.papikosbe.service.transaction;

import id.ac.ui.cs.advprog.papikosbe.model.Payment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PaymentService {
    Payment create(Payment payment);
    List<Payment> findAll();
    Payment findById(UUID id);
    List<Payment> findByUserId(UUID id);
    List<Payment> findByDate(LocalDateTime date);
}
