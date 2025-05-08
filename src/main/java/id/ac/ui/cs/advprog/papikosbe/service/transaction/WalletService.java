package id.ac.ui.cs.advprog.papikosbe.service.transaction;

import id.ac.ui.cs.advprog.papikosbe.model.Wallet;

import java.util.List;
import java.util.UUID;

public interface WalletService {
    Wallet create(UUID userId);
    List<Wallet> findAll();
    Wallet findById(UUID id);
    Wallet edit(UUID id, Wallet wallet);
    void delete(UUID id);
}
