package id.ac.ui.cs.advprog.papikosbe.controller.transaction;

import id.ac.ui.cs.advprog.papikosbe.dto.*;
import id.ac.ui.cs.advprog.papikosbe.enums.TransactionStatus;
import id.ac.ui.cs.advprog.papikosbe.enums.TransactionType;
import id.ac.ui.cs.advprog.papikosbe.model.user.Owner;
import id.ac.ui.cs.advprog.papikosbe.model.user.Tenant;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Payment;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.TopUp;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Transaction;
import id.ac.ui.cs.advprog.papikosbe.service.transaction.TransactionService;
import id.ac.ui.cs.advprog.papikosbe.util.AuthenticationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {
    @Mock
    AuthenticationUtils authenticationUtils;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private UUID transactionId;
    private UUID tenantId;
    private UUID ownerId;
    private Tenant tenant;
    private Owner owner;
    private Payment payment;
    private TopUp topUp;
    private LocalDateTime createdAt;

    @BeforeEach
    void setUp() {
        transactionId = UUID.randomUUID();
        tenantId = UUID.randomUUID();
        ownerId = UUID.randomUUID();
        createdAt = LocalDateTime.now();

        tenant = Tenant.builder().email("tenant@example.com").password("tenantpass").build();
        tenant.setId(tenantId);

        owner = Owner.builder().email("owner@example.com").password("ownerpass").build();
        owner.setId(ownerId);

        payment = new Payment();
        payment.setId(transactionId);
        payment.setAmount(new BigDecimal("100.00"));
        payment.setStatus(TransactionStatus.PENDING);
        payment.setCreatedAt(createdAt);
        payment.setUser(tenant);
        payment.setOwner(owner);

        topUp = new TopUp();
        topUp.setId(transactionId);
        topUp.setAmount(new BigDecimal("50.00"));
        topUp.setStatus(TransactionStatus.COMPLETED);
        topUp.setCreatedAt(createdAt);
        topUp.setUser(tenant);
    }

    @Test
    void getTransactionById_Success() throws Exception {
        when(transactionService.getTransactionById(transactionId)).thenReturn(payment);

        ResponseEntity<TransactionResponse> response = transactionController.getTransactionById(transactionId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(transactionId, response.getBody().getId());
        assertEquals(new BigDecimal("100.00"), response.getBody().getAmount());
        assertEquals(TransactionStatus.PENDING, response.getBody().getStatus());
        assertEquals(TransactionType.PAYMENT, response.getBody().getType());
        assertEquals(tenantId, response.getBody().getUserId());
        assertEquals(ownerId, response.getBody().getOwnerId());
    }

    @Test
    void getTransactionById_NotFound() throws Exception {
        when(transactionService.getTransactionById(transactionId))
                .thenThrow(new RuntimeException("Transaction not found"));

        ResponseEntity<TransactionResponse> response = transactionController.getTransactionById(transactionId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getUserTransactions_Success() throws Exception {
        List<Transaction> transactions = Arrays.asList(payment, topUp);
        when(transactionService.getUserTransactions(tenantId)).thenReturn(transactions);

        ResponseEntity<List<TransactionResponse>> response = transactionController.getUserTransactions(tenantId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(TransactionType.PAYMENT, response.getBody().get(0).getType());
        assertEquals(TransactionType.TOP_UP, response.getBody().get(1).getType());
    }

    @Test
    void getUserTransactions_NotFound() throws Exception {
        when(transactionService.getUserTransactions(tenantId))
                .thenThrow(new RuntimeException("User not found"));

        ResponseEntity<List<TransactionResponse>> response = transactionController.getUserTransactions(tenantId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void createPayment_Success() throws Exception {
        PaymentRequest request = new PaymentRequest();
        request.setTenantId(tenantId);
        request.setOwnerId(ownerId);
        request.setAmount(new BigDecimal("100.00"));

        when(transactionService.createPayment(eq(tenantId), eq(ownerId), eq(new BigDecimal("100.00"))))
                .thenReturn(CompletableFuture.completedFuture(payment));

        // Direct call to synchronous method (no CompletableFuture wrapper)
        ResponseEntity<TransactionResponse> response =
                transactionController.createPayment(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(transactionId, response.getBody().getId());
        assertEquals(new BigDecimal("100.00"), response.getBody().getAmount());
        assertEquals(TransactionType.PAYMENT, response.getBody().getType());
        assertEquals(TransactionStatus.PENDING, response.getBody().getStatus());
    }

    @Test
    void createPayment_Error() throws Exception {
        PaymentRequest request = new PaymentRequest();
        request.setTenantId(tenantId);
        request.setOwnerId(ownerId);
        request.setAmount(new BigDecimal("100.00"));

        CompletableFuture<Payment> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Insufficient funds"));

        when(transactionService.createPayment(eq(tenantId), eq(ownerId), eq(new BigDecimal("100.00"))))
                .thenReturn(failedFuture);

        // Direct call to synchronous method (no CompletableFuture wrapper)
        ResponseEntity<TransactionResponse> response =
                transactionController.createPayment(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Error processing payment", response.getBody().getStatus_message());
        assertTrue(response.getBody().getMessage().contains("Insufficient funds"));
    }

    @Test
    void getPaymentsByTenant_Success() {
        List<Payment> payments = Collections.singletonList(payment);
        when(transactionService.getPaymentsByTenant(tenantId))
                .thenReturn(payments);

        ResponseEntity<List<TransactionResponse>> response = transactionController.getPaymentsByTenant(tenantId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(TransactionType.PAYMENT, response.getBody().getFirst().getType());
        assertEquals(tenantId, response.getBody().getFirst().getUserId());
    }

    @Test
    void getPaymentsByTenant_ThrowsIllegalStateException() {
        when(transactionService.getPaymentsByTenant(tenantId))
                .thenThrow(new IllegalStateException("Unauthorized access"));

        ResponseEntity<List<TransactionResponse>> response = transactionController.getPaymentsByTenant(tenantId);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getPaymentsByTenant_ThrowsRuntimeException() {
        when(transactionService.getPaymentsByTenant(tenantId))
                .thenThrow(new RuntimeException("Tenant not found"));

        ResponseEntity<List<TransactionResponse>> response = transactionController.getPaymentsByTenant(tenantId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getPaymentsByOwner_Success() {
        List<Payment> payments = Collections.singletonList(payment);
        when(transactionService.getPaymentsByOwner(ownerId))
                .thenReturn(payments);

        ResponseEntity<List<TransactionResponse>> response = transactionController.getPaymentsByOwner(ownerId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(TransactionType.PAYMENT, response.getBody().getFirst().getType());
        assertEquals(ownerId, response.getBody().getFirst().getOwnerId());
    }

    @Test
    void getPaymentsByOwner_Error() {
        when(transactionService.getPaymentsByOwner(ownerId))
                .thenThrow(new RuntimeException("Owner not found"));

        ResponseEntity<List<TransactionResponse>> response =
                transactionController.getPaymentsByOwner(ownerId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void createTopUp_Success() throws Exception {
        // Arrange - TopUpRequest no longer has userId
        TopUpRequest request = new TopUpRequest();
        request.setAmount(new BigDecimal("50.00"));

        // Mock Authentication object
        Authentication authentication = mock(Authentication.class);

        // Mock AuthenticationUtils to return the tenant ID
        when(authenticationUtils.getUserIdFromAuth(authentication)).thenReturn(tenantId);

        // Mock the service call
        when(transactionService.createTopUp(eq(tenantId), eq(new BigDecimal("50.00"))))
                .thenReturn(CompletableFuture.completedFuture(topUp));

        // Act - Direct call to synchronous method (no CompletableFuture wrapper)
        ResponseEntity<TransactionResponse> response =
                transactionController.createTopUp(request, authentication);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(transactionId, response.getBody().getId());
        assertEquals(new BigDecimal("50.00"), response.getBody().getAmount());
        assertEquals(TransactionType.TOP_UP, response.getBody().getType());
        assertEquals(TransactionStatus.COMPLETED, response.getBody().getStatus());

        // Verify that AuthenticationUtils was called
        verify(authenticationUtils).getUserIdFromAuth(authentication);
        verify(transactionService).createTopUp(tenantId, new BigDecimal("50.00"));
    }

    @Test
    void createTopUp_Error() throws Exception {
        // Arrange
        TopUpRequest request = new TopUpRequest();
        request.setAmount(new BigDecimal("50.00"));

        Authentication authentication = mock(Authentication.class);
        when(authenticationUtils.getUserIdFromAuth(authentication)).thenReturn(tenantId);

        // Prepare failed future for the service layer
        CompletableFuture<TopUp> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Top-up processing failed"));

        when(transactionService.createTopUp(eq(tenantId), eq(new BigDecimal("50.00"))))
                .thenReturn(failedFuture);

        // Act - Direct call to synchronous method (no .get() needed)
        ResponseEntity<TransactionResponse> response =
                transactionController.createTopUp(request, authentication);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Error processing top-up", response.getBody().getStatus_message());
        assertTrue(response.getBody().getMessage().contains("Top-up processing failed"));

        // Verify interactions
        verify(authenticationUtils).getUserIdFromAuth(authentication);
        verify(transactionService).createTopUp(tenantId, new BigDecimal("50.00"));
    }

    @Test
    void getTopUpsByUser_Success() {
        List<TopUp> topUps = Collections.singletonList(topUp);
        when(transactionService.getTopUpsByUser(tenantId))
                .thenReturn(topUps);

        ResponseEntity<List<TransactionResponse>> response =
                transactionController.getTopUpsByUser(tenantId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(TransactionType.TOP_UP, response.getBody().getFirst().getType());
        System.out.println("User ID: " + tenantId);
        System.out.println("Response: " + response.getBody());
        assertEquals(tenantId, response.getBody().getFirst().getUserId());
    }

    @Test
    void refundPayment_Success() throws Exception {
        // Arrange - RefundRequest setup
        UUID paymentId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        RefundRequest request = new RefundRequest();
        request.setPaymentId(paymentId);
        request.setRequesterId(requesterId);

        // Prepare the mock Payment and RefundResponse
        Payment refundedPayment = new Payment();
        refundedPayment.setId(paymentId);
        refundedPayment.setAmount(new BigDecimal("100.00"));
        refundedPayment.setStatus(TransactionStatus.REFUNDED);

        RefundResponse refundResponse = new RefundResponse(refundedPayment);

        // Mock the service layer to return a completed future with the refunded payment
        CompletableFuture<Payment> completedFuture = CompletableFuture.completedFuture(refundedPayment);
        when(transactionService.refundPayment(paymentId, requesterId)).thenReturn(completedFuture);

        // Act - Direct call to controller
        ResponseEntity<?> response = transactionController.refundPayment(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof CompletableFuture);

        CompletableFuture<RefundResponse> futureResponse = (CompletableFuture<RefundResponse>) response.getBody();
        // Instead of calling getPayment(), directly access fields from RefundResponse
        assertEquals(TransactionStatus.REFUNDED, futureResponse.get().getStatus());
        assertEquals(new BigDecimal("100.00"), futureResponse.get().getAmount());
    }


    @Test
    void refundPayment_Error() throws Exception {
        // Arrange - RefundRequest setup
        UUID paymentId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        RefundRequest request = new RefundRequest();
        request.setPaymentId(paymentId);
        request.setRequesterId(requesterId);

        // Mock service layer to throw an exception
        when(transactionService.refundPayment(paymentId, requesterId))
                .thenThrow(new RuntimeException("Refund processing failed"));

        // Act - Direct call to controller
        ResponseEntity<?> response = transactionController.refundPayment(request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        Map<String, Object> errorResponse = (Map<String, Object>) response.getBody();
        assertEquals("Refund processing failed", errorResponse.get("error"));
    }

}