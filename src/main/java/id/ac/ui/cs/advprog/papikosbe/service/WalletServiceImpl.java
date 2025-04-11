package id.ac.ui.cs.advprog.papikosbe.service;

import id.ac.ui.cs.advprog.papikosbe.model.Wallet;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WalletServiceImpl implements WalletService {

    private final Map<UUID, Wallet> walletRepository = new HashMap<>();

    @Override
    public Wallet create(Wallet wallet) {
        walletRepository.put(wallet.getId(), wallet);
        return wallet;
    }

    @Override
    public List<Wallet> findAll() {
        return new ArrayList<>(walletRepository.values());
    }

    @Override
    public Wallet findById(UUID id) {
        return walletRepository.get(id);
    }

    @Override
    public Wallet edit(UUID id, Wallet wallet) {
        Wallet existing = walletRepository.get(id);
        if (existing != null) {
            existing.setBalance(wallet.getBalance());
            return existing;
        }
        return null;
    }

    @Override
    public void delete(UUID id) {
        walletRepository.remove(id);
    }
}
