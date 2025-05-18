package id.ac.ui.cs.advprog.papikosbe.service.transaction;

import id.ac.ui.cs.advprog.papikosbe.factory.WalletFactory;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Wallet;
import id.ac.ui.cs.advprog.papikosbe.model.user.User;
import id.ac.ui.cs.advprog.papikosbe.repository.user.UserRepository;
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

    @Autowired
    private UserRepository userRepository;

    @Override
    public Wallet create(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = walletFactory.createWallet(user);
        return walletRepository.save(wallet);
    }

    @Override
    public List<Wallet> findAll() {
        return walletRepository.findAll();
    }

    @Override
    public Wallet findById(UUID id) {
        return walletRepository.findById(id).orElse(null);
    }

    @Override
    public Wallet findByUserId(UUID userId) {
        return walletRepository.findByUserId(userId).orElse(null);
    }

    @Override
    public Wallet edit(UUID id, Wallet updatedWallet) {
        Optional<Wallet> optionalWallet = walletRepository.findById(id);
        if (optionalWallet.isPresent()) {
            Wallet existingWallet = optionalWallet.get();
            existingWallet.setBalance(updatedWallet.getBalance());
            return walletRepository.save(existingWallet);
        }
        return null;
    }

    @Override
    public void delete(UUID id) {
        walletRepository.deleteById(id);
    }
}
