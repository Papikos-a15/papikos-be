package id.ac.ui.cs.advprog.papikosbe.service;

import id.ac.ui.cs.advprog.papikosbe.factory.PaymentFactory;
import id.ac.ui.cs.advprog.papikosbe.model.Payment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final Map<UUID, Payment> paymentRepository = new HashMap<>();
    private final PaymentFactory paymentFactory;

    public PaymentServiceImpl(PaymentFactory paymentFactory) {
        this.paymentFactory = paymentFactory;
    }

    public Payment createPayment(UUID userId, UUID ownerId, BigDecimal amount) {
        Payment payment = paymentFactory.createPayment(userId, ownerId, amount);
        return create(payment);
    }

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

    @Override
    public List<Payment> findByUserId(UUID userId) {
        return paymentRepository.values().stream()
                .filter(payment -> payment.getUserId().equals(userId))
                .toList();
    }

    @Override
    public List<Payment> findByDate(LocalDateTime date) {
        return paymentRepository.values().stream()
                .filter(payment -> payment.getTimestamp().toLocalDate().equals(date.toLocalDate()))
                .toList();
    }
}