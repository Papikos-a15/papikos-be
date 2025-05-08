package id.ac.ui.cs.advprog.papikosbe.service.transaction;

import id.ac.ui.cs.advprog.papikosbe.factory.TransactionFactory;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.TopUp;
import id.ac.ui.cs.advprog.papikosbe.factory.TopUpFactory;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Transaction;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Wallet;
import id.ac.ui.cs.advprog.papikosbe.repository.transaction.TopUpRepository;
import id.ac.ui.cs.advprog.papikosbe.enums.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class TopUpServiceImpl implements TopUpService {

    private final TopUpRepository topUpRepository;
    private final TopUpFactory topUpFactory;
    private final TransactionFactory transactionFactory;
    private final TransactionService transactionService;
    private final WalletService walletService;

    @Autowired
    public TopUpServiceImpl(TopUpFactory topUpFactory, TopUpRepository topUpRepository,
                            TransactionFactory transactionFactory, TransactionService transactionService,
                            WalletService walletService) {
        this.topUpFactory = topUpFactory;
        this.topUpRepository = topUpRepository;
        this.transactionFactory = transactionFactory;
        this.transactionService = transactionService;
        this.walletService = walletService;
    }

    @Override
    public TopUp createTopUp(UUID userId, BigDecimal amount) {
        TopUp topUp = TopUpFactory.createTopUp(userId, amount);

        Transaction transaction = transactionFactory.createTransaction(userId, amount, TransactionType.TOP_UP);
        transactionService.create(transaction);

        Wallet wallet = walletService.findByUserId(userId);
        wallet.setBalance(wallet.getBalance().add(amount));
        walletService.edit(wallet.getId(), wallet);

        return create(topUp);
    }

    @Override
    public TopUp create(TopUp topUp) {
        topUpRepository.save(topUp);
        return topUp;
    }

    @Override
    public List<TopUp> findAll() {
        return topUpRepository.findAll();
    }

    @Override
    public TopUp findById(UUID id) {
        return topUpRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("TopUp with ID " + id + " not found"));
    }

    @Override
    public List<TopUp> findByUserId(UUID userId) {
        return topUpRepository.findByUserId(userId);
    }

    @Override
    public List<TopUp> findByDate(LocalDateTime date) {
        return topUpRepository.findByDate(date);
    }
}
