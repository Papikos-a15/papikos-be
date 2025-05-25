package id.ac.ui.cs.advprog.papikosbe.model.transaction;
import id.ac.ui.cs.advprog.papikosbe.enums.TransactionStatus;

import id.ac.ui.cs.advprog.papikosbe.enums.TransactionType;
import id.ac.ui.cs.advprog.papikosbe.model.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "transaction_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
public abstract class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status = TransactionStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "transaction_type", insertable = false, updatable = false)
    private TransactionType type;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Transient
    public final TransactionStatus process(Wallet actorWallet, Wallet secondPartyWallet) {
        try {
            validateTransaction(actorWallet);
            doProcess(actorWallet, secondPartyWallet);
            this.status = TransactionStatus.COMPLETED;
        } catch (Exception e) {
            this.status = TransactionStatus.FAILED;
        }
        return status;
    }

    @Transient
    protected abstract void validateTransaction(Wallet actorWallet) throws Exception;

    @Transient
    protected abstract void doProcess(Wallet actorWallet, Wallet secondPartyWallet) throws Exception;
}
