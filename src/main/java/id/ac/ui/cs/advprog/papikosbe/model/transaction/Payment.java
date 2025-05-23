package id.ac.ui.cs.advprog.papikosbe.model.transaction;

import id.ac.ui.cs.advprog.papikosbe.enums.TransactionStatus;
import id.ac.ui.cs.advprog.papikosbe.enums.WalletStatus;
import id.ac.ui.cs.advprog.papikosbe.model.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@DiscriminatorValue("PAYMENT")
@Getter
@Setter
public class Payment extends Transaction {

    // Pemilik kos sebagai penerima
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    private LocalDateTime paidDate;

    public Payment() {
        // JPA will use this constructor to instantiate the entity
    }

    public Payment(UUID id, BigDecimal amount, User user) {
        this.setId(id);
        this.setAmount(amount);
        this.setUser(user);
        this.setStatus(TransactionStatus.PENDING);
        this.setCreatedAt(LocalDateTime.now());
    }

    @Override
    protected void validateTransaction(Wallet tenantWallet) throws Exception {
        if (getUser() == null) {
            throw new Exception("Tenant tidak ditemukan");
        }
        if (owner == null) {
            throw new Exception("Owner tidak ditemukan");
        }
        if (tenantWallet == null || tenantWallet.getStatus() != WalletStatus.ACTIVE) {
            throw new Exception("Wallet tenant tidak valid");
        }
        if (tenantWallet.getBalance().compareTo(getAmount()) < 0) {
            throw new Exception("Saldo tidak mencukupi");
        }
    }

    @Override
    protected void doProcess(Wallet tenantWallet, Wallet ownerWallet) throws Exception {
        tenantWallet.setBalance(tenantWallet.getBalance().subtract(getAmount()));
        ownerWallet.setBalance(ownerWallet.getBalance().add(getAmount()));
        setPaidDate(LocalDateTime.now());
    }
}