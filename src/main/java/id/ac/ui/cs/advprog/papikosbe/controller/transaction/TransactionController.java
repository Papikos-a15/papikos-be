package id.ac.ui.cs.advprog.papikosbe.controller.transaction;

import id.ac.ui.cs.advprog.papikosbe.dto.*;
import id.ac.ui.cs.advprog.papikosbe.enums.TransactionType;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Payment;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.TopUp;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Transaction;
import id.ac.ui.cs.advprog.papikosbe.service.transaction.TransactionService;
import id.ac.ui.cs.advprog.papikosbe.util.AuthenticationUtils;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    AuthenticationUtils authenticationUtils;
    TransactionService transactionService;

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable UUID id) {
        try {
            Transaction transaction = transactionService.getTransactionById(id);
            TransactionResponse response = mapToTransactionResponse(transaction);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TransactionResponse>> getUserTransactions(@PathVariable UUID userId) {
        System.out.println("FETCH USER TRANSACTIONS");
        System.out.println("userId = " + userId);
        try {
            List<Transaction> transactions = transactionService.getUserTransactions(userId);
            List<TransactionResponse> responses = transactions.stream()
                    .map(this::mapToTransactionResponse)
                    .toList(); // cleaner and more idiomatic
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/payment")
    public ResponseEntity<TransactionResponse> createPayment(@RequestBody PaymentRequest paymentRequest) {
        try {
            // Wait for the async service operation to complete
            Payment payment = transactionService.createPayment(
                    paymentRequest.getTenantId(),
                    paymentRequest.getOwnerId(),
                    paymentRequest.getAmount()
            ).get();

            TransactionResponse response = mapToTransactionResponse(payment);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception ex) {
            if (ex instanceof InterruptedException) {
                Thread.currentThread().interrupt(); // Re-interrupt
            }

            String errorMessage = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
            TransactionResponse errorResponse = new TransactionResponse("Error processing payment", errorMessage);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

    }

    @GetMapping("/payment/tenant/{tenantId}")
    public CompletableFuture<ResponseEntity<List<TransactionResponse>>> getPaymentsByTenant(@PathVariable UUID tenantId) {
        return transactionService.getPaymentsByTenant(tenantId)
                .thenApply(payments -> payments.stream()
                        .map(this::mapToTransactionResponse)
                        .collect(Collectors.toList()))
                .thenApply(responses -> ResponseEntity.ok(responses))
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @GetMapping("/payment/owner/{ownerId}")
    public CompletableFuture<ResponseEntity<List<TransactionResponse>>> getPaymentsByOwner(@PathVariable UUID ownerId) {
        return transactionService.getPaymentsByOwner(ownerId)
                .thenApply(payments -> payments.stream()
                        .map(this::mapToTransactionResponse)
                        .collect(Collectors.toList()))
                .thenApply(responses -> ResponseEntity.ok(responses))
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PostMapping("/topup")
    public ResponseEntity<TransactionResponse> createTopUp(
            @RequestBody TopUpRequest topUpRequest,
            Authentication authentication) {

        UUID userId = authenticationUtils.getUserIdFromAuth(authentication);

        try {
            // Wait for the async operation to complete
            TopUp topUp = transactionService.createTopUp(userId, topUpRequest.getAmount()).get();
            TransactionResponse response = mapToTransactionResponse(topUp);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception ex) {
            if (ex instanceof InterruptedException) {
                Thread.currentThread().interrupt(); // Re-interrupt the thread
            }

            TransactionResponse errorResponse = new TransactionResponse(
                    "Error processing top-up",
                    ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @GetMapping("/topup/user/{userId}")
    public CompletableFuture<ResponseEntity<List<TransactionResponse>>> getTopUpsByUser(@PathVariable UUID userId) {
        return transactionService.getTopUpsByUser(userId)
                .thenApply(topUps -> topUps.stream()
                        .map(this::mapToTransactionResponse)
                        .collect(Collectors.toList()))
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PostMapping("/refund")
    public ResponseEntity<?> refundPayment(@RequestBody RefundRequest request) {
        try {
            CompletableFuture<Payment> result = transactionService
                    .refundPayment(request.getPaymentId(), request.getRequesterId());

            return ResponseEntity.ok().body(
                    result.thenApply(RefundResponse::new)
            );
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    // Helper method to map Transaction to TransactionResponse
    private TransactionResponse mapToTransactionResponse(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setAmount(transaction.getAmount());
        response.setStatus(transaction.getStatus());
        response.setCreatedAt(transaction.getCreatedAt());
        response.setUserId(transaction.getUser().getId());

        if (transaction instanceof Payment) {
            Payment payment = (Payment) transaction;
            response.setType(TransactionType.PAYMENT);
            response.setOwnerId(payment.getOwner().getId());
        } else if (transaction instanceof TopUp) {
            response.setType(TransactionType.TOP_UP);
        }

        return response;
    }
}
