package id.ac.ui.cs.advprog.papikosbe.repository.transaction;

import id.ac.ui.cs.advprog.papikosbe.model.transaction.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {
    Optional<Wallet> findByUserId(UUID userId);
}
