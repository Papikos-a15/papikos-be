package id.ac.ui.cs.advprog.papikosbe.service.transaction;

import id.ac.ui.cs.advprog.papikosbe.model.transaction.Wallet;

import java.util.List;
import java.util.UUID;

public interface WalletService {
    Wallet create(UUID userId);
    List<Wallet> findAll();
    Wallet findById(UUID id);
    Wallet findByUserId(UUID userId);
    Wallet edit(UUID id, Wallet wallet);
    void delete(UUID id);
}
