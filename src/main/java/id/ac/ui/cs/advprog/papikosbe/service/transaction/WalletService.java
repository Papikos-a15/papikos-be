package id.ac.ui.cs.advprog.papikosbe.service.transaction;

import id.ac.ui.cs.advprog.papikosbe.model.transaction.Wallet;
import id.ac.ui.cs.advprog.papikosbe.model.user.User;

import java.util.List;
import java.util.UUID;

public interface WalletService {
    Wallet create(UUID userId);
    List<Wallet> findAll();
    Wallet findById(UUID id);
    Wallet getOrCreateWallet(User user);
    Wallet edit(UUID id, Wallet wallet);
    void delete(UUID id);
    Wallet findByUserId(UUID userId);
}
