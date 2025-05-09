package id.ac.ui.cs.advprog.papikosbe.service.transaction;

import id.ac.ui.cs.advprog.papikosbe.factory.WalletFactory;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Wallet;
import id.ac.ui.cs.advprog.papikosbe.repository.transaction.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WalletServiceImpl implements WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletFactory walletFactory;

    @Override
    public Wallet create(UUID userId) {
        Wallet wallet = walletFactory.createWallet(userId);
        return walletRepository.create(wallet);
    }

    @Override
    public List<Wallet> findAll() {
        Iterator<Wallet> iterator = walletRepository.findAll();
        List<Wallet> wallets = new ArrayList<>();
        iterator.forEachRemaining(wallets::add);
        return wallets;
    }

    @Override
    public Wallet findById(UUID id) {
        return walletRepository.findById(id).orElse(null);
    }

    @Override
    public Wallet findByUserId(UUID userId) {
        Iterator<Wallet> iterator = walletRepository.findAll();
        while (iterator.hasNext()) {
            Wallet wallet = iterator.next();
            if (wallet.getUserId().equals(userId)) {
                return wallet;
            }
        }
        return null;
    }

    @Override
    public Wallet edit(UUID id, Wallet wallet) {
        Wallet existing = walletRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setBalance(wallet.getBalance());
            return existing;
        }
        return null;
    }

    @Override
    public void delete(UUID id) {
        walletRepository.delete(id);
    }
}
