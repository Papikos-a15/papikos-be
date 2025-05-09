package id.ac.ui.cs.advprog.papikosbe.repository.transaction;

import id.ac.ui.cs.advprog.papikosbe.model.transaction.Wallet;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class WalletRepository {
    private final List<Wallet> walletData = new ArrayList<>();

    public Wallet create(Wallet wallet) {
        if (wallet.getId() == null) {
            wallet.setId(UUID.randomUUID());
        }
        walletData.add(wallet);
        return wallet;
    }

    public Wallet edit(UUID walletId, Wallet newWalletData) {
        Optional<Wallet> walletToEditOpt = findById(walletId);
        if (walletToEditOpt.isPresent()) {
            Wallet walletToEdit = walletToEditOpt.get();
            walletToEdit.setBalance(newWalletData.getBalance());
            return walletToEdit;
        }
        return null;
    }

    public Iterator<Wallet> findAll() {
        return walletData.iterator();
    }

    public Optional<Wallet> findById(UUID walletId) {
        for (Wallet wallet : walletData) {
            if (wallet.getId().equals(walletId)) {
                return Optional.of(wallet);
            }
        }
        return Optional.empty();
    }

    public Optional<Wallet> findByUserId(UUID userId) {
        for (Wallet wallet : walletData) {
            if (wallet.getUserId().equals(userId)) {
                return Optional.of(wallet);
            }
        }
        return Optional.empty();
    }

    public Wallet updateBalance(Wallet updatedWallet) {
        Optional<Wallet> walletOptional = findById(updatedWallet.getId());
        if (walletOptional.isPresent()) {
            Wallet existingWallet = walletOptional.get();
            existingWallet.setBalance(updatedWallet.getBalance());
            return existingWallet;
        }
        return null;
    }

    public void delete(UUID walletId) {
        findById(walletId).ifPresent(walletData::remove);
    }
}