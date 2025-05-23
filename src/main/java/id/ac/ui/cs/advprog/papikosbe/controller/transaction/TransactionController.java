package id.ac.ui.cs.advprog.papikosbe.controller.transaction;

import id.ac.ui.cs.advprog.papikosbe.dto.PaymentRequest;
import id.ac.ui.cs.advprog.papikosbe.dto.TopUpRequest;
import id.ac.ui.cs.advprog.papikosbe.dto.TransactionResponse;
import id.ac.ui.cs.advprog.papikosbe.enums.TransactionType;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Payment;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.TopUp;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Transaction;
import id.ac.ui.cs.advprog.papikosbe.service.transaction.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
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
        try {
            List<Transaction> transactions = transactionService.getUserTransactions(userId);
            List<TransactionResponse> responses = transactions.stream()
                    .map(this::mapToTransactionResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/payment")
    public CompletableFuture<ResponseEntity<TransactionResponse>> createPayment(@RequestBody PaymentRequest paymentRequest) throws Exception {
        return transactionService.createPayment(paymentRequest.getTenantId(), paymentRequest.getOwnerId(), paymentRequest.getAmount())
                .thenApply(payment -> {
                    TransactionResponse response = mapToTransactionResponse(payment);
                    return ResponseEntity.status(HttpStatus.CREATED).body(response);
                })
                .exceptionally(ex -> {
                    TransactionResponse errorResponse = new TransactionResponse("Error processing payment", ex.getMessage());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
                });
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
    public CompletableFuture<ResponseEntity<TransactionResponse>> createTopUp(@RequestBody TopUpRequest topUpRequest) throws Exception {
        // Asynchronous service call
        return transactionService.createTopUp(topUpRequest.getUserId(), topUpRequest.getAmount())
                .thenApply(topUp -> {
                    // Map to response and set status code to CREATED (201)
                    TransactionResponse response = mapToTransactionResponse(topUp);
                    return ResponseEntity.status(HttpStatus.CREATED).body(response);
                })
                .exceptionally(ex -> {
                    // Handle exception and return error response
                    TransactionResponse errorResponse = new TransactionResponse("Error processing top-up", ex.getMessage());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
                });
    }

    @GetMapping("/topup/user/{userId}")
    public CompletableFuture<ResponseEntity<List<TransactionResponse>>> getTopUpsByUser(@PathVariable UUID userId) {
        return transactionService.getTopUpsByUser(userId)
                .thenApply(topUps -> topUps.stream()
                        .map(this::mapToTransactionResponse)
                        .collect(Collectors.toList()))
                .thenApply(responses -> ResponseEntity.ok(responses))
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
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
