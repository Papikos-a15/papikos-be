package id.ac.ui.cs.advprog.papikosbe.service.transaction;

import id.ac.ui.cs.advprog.papikosbe.factory.DefaultTransactionFactory;
import id.ac.ui.cs.advprog.papikosbe.factory.PaymentFactory;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Payment;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Transaction;
import org.springframework.stereotype.Service;

import id.ac.ui.cs.advprog.papikosbe.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final Map<UUID, Payment> paymentRepository = new HashMap<>();
    private final PaymentFactory paymentFactory;
    private final DefaultTransactionFactory transactionFactory;
    private final TransactionService transactionService;

    public PaymentServiceImpl(PaymentFactory paymentFactory, DefaultTransactionFactory transactionFactory, TransactionService transactionService) {
        this.paymentFactory = paymentFactory;
        this.transactionFactory = transactionFactory;
        this.transactionService = transactionService;
    }

    public Payment createPayment(UUID userId, UUID ownerId, BigDecimal amount) {
        Payment payment = paymentFactory.createPayment(userId, ownerId, amount);

        Transaction transaction = transactionFactory.createTransaction(
                userId, amount, TransactionType.PAYMENT
        );
        transactionService.create(transaction);
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