package id.ac.ui.cs.advprog.papikosbe.model.transaction;

import id.ac.ui.cs.advprog.papikosbe.enums.WalletStatus;
import id.ac.ui.cs.advprog.papikosbe.model.user.Owner;
import id.ac.ui.cs.advprog.papikosbe.model.user.Tenant;
import id.ac.ui.cs.advprog.papikosbe.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    private Payment payment;
    private Wallet tenantWallet;
    private Wallet ownerWallet;

    @BeforeEach
    void setUp() {
        User tenant = Tenant.builder()
                .email("tenant@example.com")
                .password("tenantpass")
                .build();

        User owner = Owner.builder()
                .email("owner@example.com")
                .password("ownerpass")
                .build();

        tenantWallet = new Wallet();
        tenantWallet.setUser(tenant);
        tenantWallet.setStatus(WalletStatus.ACTIVE);
        tenantWallet.setBalance(new BigDecimal("100000"));

        ownerWallet = new Wallet();
        ownerWallet.setUser(owner);
        ownerWallet.setStatus(WalletStatus.ACTIVE);
        ownerWallet.setBalance(new BigDecimal("50000"));

        payment = new Payment();
        payment.setUser(tenant);
        payment.setOwner(owner);
        payment.setAmount(new BigDecimal("30000"));
    }


    @Test
    void testValidateTransaction_Valid() {
        assertDoesNotThrow(() -> payment.validateTransaction(tenantWallet));
    }

    @Test
    void testValidateTransaction_UserIsNull() {
        payment.setUser(null);
        Exception exception = assertThrows(Exception.class, () -> payment.validateTransaction(tenantWallet));
        assertEquals("Tenant tidak ditemukan", exception.getMessage());
    }

    @Test
    void testValidateTransaction_OwnerIsNull() {
        payment.setOwner(null);
        Exception exception = assertThrows(Exception.class, () -> payment.validateTransaction(tenantWallet));
        assertEquals("Owner tidak ditemukan", exception.getMessage());
    }

    @Test
    void testValidateTransaction_WalletIsNull() {
        Exception exception = assertThrows(Exception.class, () -> payment.validateTransaction(null));
        assertEquals("Wallet tenant tidak valid", exception.getMessage());
    }

    @Test
    void testValidateTransaction_InactiveWallet() {
        tenantWallet.setStatus(WalletStatus.CLOSED);
        Exception exception = assertThrows(Exception.class, () -> payment.validateTransaction(tenantWallet));
        assertEquals("Wallet tenant tidak valid", exception.getMessage());
    }

    @Test
    void testValidateTransaction_InsufficientBalance() {
        payment.setAmount(new BigDecimal("150000"));
        Exception exception = assertThrows(Exception.class, () -> payment.validateTransaction(tenantWallet));
        assertEquals("Saldo tidak mencukupi", exception.getMessage());
    }

    @Test
    void testDoProcess() throws Exception {
        BigDecimal initialTenantBalance = tenantWallet.getBalance();
        BigDecimal initialOwnerBalance = ownerWallet.getBalance();

        payment.doProcess(tenantWallet, ownerWallet);

        assertEquals(initialTenantBalance.subtract(payment.getAmount()), tenantWallet.getBalance());
        assertEquals(initialOwnerBalance.add(payment.getAmount()), ownerWallet.getBalance());
        assertNotNull(payment.getPaidDate());
        assertTrue(payment.getPaidDate().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
}