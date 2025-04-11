package id.ac.ui.cs.advprog.papikosbe.service;

import id.ac.ui.cs.advprog.papikosbe.model.Payment;

import java.util.List;
import java.util.UUID;

public interface PaymentService {
    Payment create(Payment payment);
    List<Payment> findAll();
    Payment findById(UUID id);
}
