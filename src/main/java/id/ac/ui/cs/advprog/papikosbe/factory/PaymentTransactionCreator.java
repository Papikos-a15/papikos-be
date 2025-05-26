package id.ac.ui.cs.advprog.papikosbe.factory;

import id.ac.ui.cs.advprog.papikosbe.model.transaction.Payment;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Transaction;
import id.ac.ui.cs.advprog.papikosbe.model.user.User;
import id.ac.ui.cs.advprog.papikosbe.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class PaymentTransactionCreator implements TransactionCreator {

    private final UserRepository userRepository;

    @Autowired
    public PaymentTransactionCreator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Transaction create(UUID userId, BigDecimal amount, UUID ownerId) throws Exception {
        if (ownerId == null) throw new Exception("Owner ID is required for Payment");

        User tenant = userRepository.findById(userId).orElseThrow(() -> new Exception("Tenant not found"));
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new Exception("Owner not found"));

        Payment payment = new Payment();
        payment.setUser(tenant);
        payment.setOwner(owner);
        payment.setAmount(amount);
        return payment;
    }
}

