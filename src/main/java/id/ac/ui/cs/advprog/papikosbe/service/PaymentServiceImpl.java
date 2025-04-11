package id.ac.ui.cs.advprog.papikosbe.service;

import id.ac.ui.cs.advprog.papikosbe.model.Payment;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final Map<UUID, Payment> paymentRepository = new HashMap<>();

    @Override
    public Payment create(Payment payment) {
        paymentRepository.put(payment.getId(), payment);
        return payment;
    }

    @Override
    public List<Payment> findAll() {
        return new ArrayList<>(paymentRepository.values());
    }

    @Override
    public Payment findById(UUID id) {
        return paymentRepository.get(id);
    }
}