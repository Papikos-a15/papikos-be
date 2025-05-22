package id.ac.ui.cs.advprog.papikosbe.service.transaction;

import id.ac.ui.cs.advprog.papikosbe.enums.TransactionStatus;
import id.ac.ui.cs.advprog.papikosbe.enums.TransactionType;
import id.ac.ui.cs.advprog.papikosbe.enums.WalletStatus;
import id.ac.ui.cs.advprog.papikosbe.factory.TransactionFactory;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Payment;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.TopUp;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Transaction;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Wallet;
import id.ac.ui.cs.advprog.papikosbe.model.user.User;
import id.ac.ui.cs.advprog.papikosbe.repository.transaction.TransactionRepository;
import id.ac.ui.cs.advprog.papikosbe.repository.transaction.WalletRepository;
import id.ac.ui.cs.advprog.papikosbe.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionFactory transactionFactory;

    @Override
    public Transaction getTransactionById(UUID id) throws Exception {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new Exception("Transaksi tidak ditemukan"));
    }

    @Override
    public List<Transaction> getUserTransactions(UUID userId) throws Exception {
        List<Payment> payments = transactionRepository.findPaymentsByUser(userId);
        List<TopUp> topUps = transactionRepository.findTopUpsByUser(userId);

        List<Transaction> all = Stream.concat(payments.stream(), topUps.stream())
                .sorted(Comparator.comparing(Transaction::getCreatedAt).reversed())
                .collect(Collectors.toList());

        return all;
    }

    @Override
    public List<Transaction> getTransactionByDate(LocalDateTime date) {
        return transactionRepository.findByDate(LocalDate.from(date));
    }

    /*** Payment Methods ***/
    @Override
    public Payment createPayment(UUID tenantId, UUID ownerId, BigDecimal amount) throws Exception {
        validatePayment(tenantId, ownerId, amount);

        Payment payment = (Payment) transactionFactory.createTransaction(
                TransactionType.PAYMENT, tenantId, amount, ownerId
        );

        Wallet tenantWallet = walletRepository.findByUserId(tenantId).get();
        Wallet ownerWallet = walletRepository.findByUserId(ownerId).get();

        TransactionStatus status = payment.process(tenantWallet, ownerWallet);

        if (status == TransactionStatus.COMPLETED) {
            walletRepository.save(tenantWallet);
            walletRepository.save(ownerWallet);
            transactionRepository.save(payment);
        } else {
            throw new Exception("Pembayaran gagal: " + status);
        }

        return payment;
    }

    /*** TopUp Methods ***/
    @Override
    public TopUp createTopUp(UUID userId, BigDecimal amount) throws Exception {
        // Validate top up details
        validateTopUp(userId, amount);

        TopUp topUp = (TopUp) transactionFactory.createTransaction(
                TransactionType.TOP_UP, userId, amount, null
        );

        Wallet userWallet = walletRepository.findByUserId(userId).get();

        TransactionStatus status = topUp.process(userWallet, null);

        if (status == TransactionStatus.COMPLETED) {
            walletRepository.save(userWallet);
            transactionRepository.save(topUp);
        } else {
            throw new Exception("Top up gagal: " + status);
        }

        return topUp;
    }

    /*** Validation Methods ***/
    public void validatePayment(UUID tenantId, UUID ownerId, BigDecimal amount) throws Exception {
        System.out.println("Cari Tenant ID: " + tenantId);
        System.out.println("Jumlah user dalam repo: " + userRepository.findAll().size());

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception("Jumlah pembayaran harus lebih dari 0");
        }

        if (tenantId.equals(ownerId)) {
            throw new Exception("Tenant dan owner tidak boleh sama");
        }

        User tenant = userRepository.findById(tenantId)
                .orElseThrow(() -> new Exception("Tenant tidak ditemukan"));

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new Exception("Owner tidak ditemukan"));

        Wallet tenantWallet = walletRepository.findByUserId(tenantId)
                .orElseThrow(() -> new Exception("Wallet tenant tidak ditemukan"));

        Wallet ownerWallet = walletRepository.findByUserId(ownerId)
                .orElseThrow(() -> new Exception("Wallet owner tidak ditemukan"));

        if (tenantWallet.getStatus() != WalletStatus.ACTIVE) {
            throw new Exception("Wallet tenant tidak aktif");
        }

        if (ownerWallet.getStatus() != WalletStatus.ACTIVE) {
            throw new Exception("Wallet owner tidak aktif");
        }

        if (tenantWallet.getBalance().compareTo(amount) < 0) {
            throw new Exception("Saldo tenant tidak mencukupi");
        }
    }

    public void validateTopUp(UUID userId, BigDecimal amount) throws Exception {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception("Jumlah top up harus lebih dari 0");
        }

        if (amount.compareTo(new BigDecimal("10000")) < 0) {
            throw new Exception("Minimum top up adalah Rp 10.000");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User tidak ditemukan"));

        Wallet userWallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new Exception("Wallet user tidak ditemukan"));

        if (userWallet.getStatus() != WalletStatus.ACTIVE) {
            throw new Exception("Wallet user tidak aktif");
        }
    }

    @Override
    public List<Payment> getPaymentsByTenant(UUID tenantId) {
        return transactionRepository.findPaymentsByTenant(tenantId);
    }

    @Override
    public List<Payment> getPaymentsByOwner(UUID ownerId) {
        return transactionRepository.findPaymentsByOwner(ownerId);
    }

    @Override
    public List<TopUp> getTopUpsByUser(UUID userId) {
        return transactionRepository.findTopUpsByUser(userId);
    }
}