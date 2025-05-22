package id.ac.ui.cs.advprog.papikosbe.factory;

import id.ac.ui.cs.advprog.papikosbe.enums.TransactionType;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Payment;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.TopUp;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Transaction;
import id.ac.ui.cs.advprog.papikosbe.model.user.Owner;
import id.ac.ui.cs.advprog.papikosbe.model.user.User;
import id.ac.ui.cs.advprog.papikosbe.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class TransactionFactory {
    private final UserRepository userRepository;

    @Autowired
    public TransactionFactory(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Transaction createTransaction(TransactionType type, UUID userId, BigDecimal amount, UUID ownerId) throws Exception {
        switch (type) {
            case TOP_UP:
                return createTopUp(userId, amount);
            case PAYMENT:
                System.out.println("CASE PAYMENT");
                if (ownerId == null) {
                    throw new Exception("Owner ID is required for Payment");
                }
                return createPayment(userId, ownerId, amount);
            default:
                throw new IllegalArgumentException("Unknown transaction type: " + type);
        }
    }

    private TopUp createTopUp(UUID userId, BigDecimal amount) throws Exception {
        TopUp topUp = new TopUp();
        topUp.setAmount(amount);
        User user = userRepository.findById(userId).orElseThrow(() -> new Exception("User not found"));
        topUp.setUser(user);
        return topUp;
    }

    private Payment createPayment(UUID tenantId, UUID ownerId, BigDecimal amount) throws Exception {
        Payment payment = new Payment();
        payment.setAmount(amount);
        User tenant = userRepository.findById(tenantId).orElseThrow(() -> new Exception("Tenant not found"));
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new Exception("Owner not found"));
        payment.setUser(tenant);
        payment.setOwner(owner);
        return payment;
    }
}