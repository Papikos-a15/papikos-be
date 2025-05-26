package id.ac.ui.cs.advprog.papikosbe.factory;

import id.ac.ui.cs.advprog.papikosbe.model.transaction.TopUp;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Transaction;
import id.ac.ui.cs.advprog.papikosbe.model.user.User;
import id.ac.ui.cs.advprog.papikosbe.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class TopUpTransactionCreator implements TransactionCreator {

    private final UserRepository userRepository;

    @Autowired
    public TopUpTransactionCreator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Transaction create(UUID userId, BigDecimal amount, UUID ownerId) throws Exception {
        User user = userRepository.findById(userId).orElseThrow(() -> new Exception("User not found"));
        TopUp topUp = new TopUp();
        topUp.setUser(user);
        topUp.setAmount(amount);
        return topUp;
    }
}

