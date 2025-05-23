package id.ac.ui.cs.advprog.papikosbe.controller.transaction;

import id.ac.ui.cs.advprog.papikosbe.dto.PaymentRequest;
import id.ac.ui.cs.advprog.papikosbe.dto.TopUpRequest;
import id.ac.ui.cs.advprog.papikosbe.dto.TransactionResponse;
import id.ac.ui.cs.advprog.papikosbe.enums.TransactionStatus;
import id.ac.ui.cs.advprog.papikosbe.enums.TransactionType;
import id.ac.ui.cs.advprog.papikosbe.model.user.Owner;
import id.ac.ui.cs.advprog.papikosbe.model.user.Tenant;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Payment;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.TopUp;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Transaction;
import id.ac.ui.cs.advprog.papikosbe.service.transaction.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

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

        CompletableFuture<ResponseEntity<TransactionResponse>> futureResponse =
                transactionController.createPayment(request);
        ResponseEntity<TransactionResponse> response = futureResponse.get();

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

        CompletableFuture<ResponseEntity<TransactionResponse>> futureResponse =
                transactionController.createPayment(request);
        ResponseEntity<TransactionResponse> response = futureResponse.get();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Error processing payment", response.getBody().getStatus_message());
        assertTrue(response.getBody().getMessage().contains("Insufficient funds"));
    }

    @Test
    void getPaymentsByTenant_Success() throws ExecutionException, InterruptedException {
        List<Payment> payments = Arrays.asList(payment);
        when(transactionService.getPaymentsByTenant(tenantId))
                .thenReturn(CompletableFuture.completedFuture(payments));

        CompletableFuture<ResponseEntity<List<TransactionResponse>>> futureResponse =
                transactionController.getPaymentsByTenant(tenantId);
        ResponseEntity<List<TransactionResponse>> response = futureResponse.get();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(TransactionType.PAYMENT, response.getBody().get(0).getType());
        assertEquals(tenantId, response.getBody().get(0).getUserId());
    }

    @Test
    void getPaymentsByTenant_Error() throws ExecutionException, InterruptedException {
        CompletableFuture<List<Payment>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Tenant not found"));

        when(transactionService.getPaymentsByTenant(tenantId)).thenReturn(failedFuture);

        CompletableFuture<ResponseEntity<List<TransactionResponse>>> futureResponse =
                transactionController.getPaymentsByTenant(tenantId);
        ResponseEntity<List<TransactionResponse>> response = futureResponse.get();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getPaymentsByOwner_Success() throws ExecutionException, InterruptedException {
        List<Payment> payments = Arrays.asList(payment);
        when(transactionService.getPaymentsByOwner(ownerId))
                .thenReturn(CompletableFuture.completedFuture(payments));

        CompletableFuture<ResponseEntity<List<TransactionResponse>>> futureResponse =
                transactionController.getPaymentsByOwner(ownerId);
        ResponseEntity<List<TransactionResponse>> response = futureResponse.get();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(TransactionType.PAYMENT, response.getBody().get(0).getType());
        assertEquals(ownerId, response.getBody().get(0).getOwnerId());
    }

    @Test
    void getPaymentsByOwner_Error() throws ExecutionException, InterruptedException {
        CompletableFuture<List<Payment>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Owner not found"));

        when(transactionService.getPaymentsByOwner(ownerId)).thenReturn(failedFuture);

        CompletableFuture<ResponseEntity<List<TransactionResponse>>> futureResponse =
                transactionController.getPaymentsByOwner(ownerId);
        ResponseEntity<List<TransactionResponse>> response = futureResponse.get();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void createTopUp_Success() throws Exception {
        TopUpRequest request = new TopUpRequest();
        request.setUserId(tenantId);
        request.setAmount(new BigDecimal("50.00"));

        when(transactionService.createTopUp(eq(tenantId), eq(new BigDecimal("50.00"))))
                .thenReturn(CompletableFuture.completedFuture(topUp));

        CompletableFuture<ResponseEntity<TransactionResponse>> futureResponse =
                transactionController.createTopUp(request);
        ResponseEntity<TransactionResponse> response = futureResponse.get();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(transactionId, response.getBody().getId());
        assertEquals(new BigDecimal("50.00"), response.getBody().getAmount());
        assertEquals(TransactionType.TOP_UP, response.getBody().getType());
        assertEquals(TransactionStatus.COMPLETED, response.getBody().getStatus());
    }

    @Test
    void createTopUp_Error() throws Exception {
        TopUpRequest request = new TopUpRequest();
        request.setUserId(tenantId);
        request.setAmount(new BigDecimal("50.00"));

        CompletableFuture<TopUp> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Payment processing failed"));

        when(transactionService.createTopUp(eq(tenantId), eq(new BigDecimal("50.00"))))
                .thenReturn(failedFuture);

        CompletableFuture<ResponseEntity<TransactionResponse>> futureResponse =
                transactionController.createTopUp(request);
        ResponseEntity<TransactionResponse> response = futureResponse.get();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Error processing top-up", response.getBody().getStatus_message());
        assertTrue(response.getBody().getMessage().contains("Payment processing failed"));
    }


    @Test
    void getTopUpsByUser_Success() throws ExecutionException, InterruptedException {
        List<TopUp> topUps = Arrays.asList(topUp);
        when(transactionService.getTopUpsByUser(tenantId))
                .thenReturn(CompletableFuture.completedFuture(topUps));

        CompletableFuture<ResponseEntity<List<TransactionResponse>>> futureResponse =
                transactionController.getTopUpsByUser(tenantId);
        ResponseEntity<List<TransactionResponse>> response = futureResponse.get();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(TransactionType.TOP_UP, response.getBody().get(0).getType());
        System.out.println("User ID: " + tenantId);
        System.out.println("Response: " + response.getBody());
        assertEquals(tenantId, response.getBody().get(0).getUserId());
    }

    @Test
    void getTopUpsByUser_Error() throws ExecutionException, InterruptedException {
        CompletableFuture<List<TopUp>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("User not found"));

        when(transactionService.getTopUpsByUser(tenantId)).thenReturn(failedFuture);

        CompletableFuture<ResponseEntity<List<TransactionResponse>>> futureResponse =
                transactionController.getTopUpsByUser(tenantId);
        ResponseEntity<List<TransactionResponse>> response = futureResponse.get();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}