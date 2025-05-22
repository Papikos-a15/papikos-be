package id.ac.ui.cs.advprog.papikosbe.model.transaction;

import id.ac.ui.cs.advprog.papikosbe.enums.TransactionStatus;
import id.ac.ui.cs.advprog.papikosbe.enums.WalletStatus;
import id.ac.ui.cs.advprog.papikosbe.model.user.User;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@DiscriminatorValue("TOPUP")
@Getter
@Setter
public class TopUp extends Transaction {

    private LocalDateTime topUpDate;

    public TopUp() {
        // JPA will use this constructor to instantiate the entity
    }

    public TopUp(UUID id, BigDecimal amount, User user) {
        this.setId(id);
        this.setAmount(amount);
        this.setUser(user);
        this.setStatus(TransactionStatus.PENDING);
        this.setCreatedAt(LocalDateTime.now());
    }

    @Override
    protected void validateTransaction(Wallet userWallet) throws Exception {
        if (getUser() == null) {
            throw new Exception("User tidak ditemukan");
        }
        if (userWallet == null || userWallet.getStatus() != WalletStatus.ACTIVE) {
            throw new Exception("Wallet user tidak valid");
        }
        if (getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception("Jumlah top up harus lebih dari 0");
        }
    }

    @Override
    protected void doProcess(Wallet userWallet, Wallet unused) throws Exception {
        userWallet.setBalance(userWallet.getBalance().add(getAmount()));
        setTopUpDate(LocalDateTime.now());
    }
}