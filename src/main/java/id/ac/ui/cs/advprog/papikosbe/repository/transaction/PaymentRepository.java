package id.ac.ui.cs.advprog.papikosbe.repository.transaction;

import id.ac.ui.cs.advprog.papikosbe.model.transaction.Payment;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PaymentRepository {
    private final List<Payment> paymentData = new ArrayList<>();

    public Payment create(Payment payment) {
        if (payment.getId() == null) {
            payment.setId(UUID.randomUUID());
        }
        paymentData.add(payment);
        return payment;
    }

    public Payment save(Payment payment) {
        Optional<Payment> existing = findById(payment.getId());
        existing.ifPresent(paymentData::remove); // Remove dulu kalo udah ada
        paymentData.add(payment); // Tambahkan data baru
        return payment;
    }

    public Iterator<Payment> findAll() {
        return paymentData.iterator();
    }

    public Optional<Payment> findById(UUID paymentId) {
        for (Payment payment : paymentData) {
            if (payment.getId().equals(paymentId)) {
                return Optional.of(payment);
            }
        }
        return Optional.empty();
    }

    public List<Payment> findByUserId(UUID userId) {
        List<Payment> result = new ArrayList<>();
        for (Payment payment : paymentData) {
            if (payment.getUserId().equals(userId)) {
                result.add(payment);
            }
        }
        return result;
    }

    public void delete(UUID paymentId) {
        findById(paymentId).ifPresent(paymentData::remove);
    }
}