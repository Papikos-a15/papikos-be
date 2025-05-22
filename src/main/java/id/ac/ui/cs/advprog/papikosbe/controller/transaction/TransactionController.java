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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getTransactionById(@PathVariable UUID id) {
        try {
            Transaction transaction = transactionService.getTransactionById(id);
            TransactionResponse response = mapToTransactionResponse(transaction);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserTransactions(@PathVariable UUID userId) {
        try {
            List<Transaction> transactions = transactionService.getUserTransactions(userId);
            List<TransactionResponse> responses = transactions.stream()
                    .map(this::mapToTransactionResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/payment")
    public ResponseEntity<?> createPayment(@RequestBody PaymentRequest paymentRequest) {
        try {
            Payment payment = transactionService.createPayment(
                    paymentRequest.getTenantId(),
                    paymentRequest.getOwnerId(),
                    paymentRequest.getAmount()
            );
            TransactionResponse response = mapToTransactionResponse(payment);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/payment/tenant/{tenantId}")
    public ResponseEntity<?> getPaymentsByTenant(@PathVariable UUID tenantId) {
        try {
            List<Payment> payments = transactionService.getPaymentsByTenant(tenantId);
            List<TransactionResponse> responses = payments.stream()
                    .map(this::mapToTransactionResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/payment/owner/{ownerId}")
    public ResponseEntity<?> getPaymentsByOwner(@PathVariable UUID ownerId) {
        try {
            List<Payment> payments = transactionService.getPaymentsByOwner(ownerId);
            List<TransactionResponse> responses = payments.stream()
                    .map(this::mapToTransactionResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/topup")
    public ResponseEntity<?> createTopUp(@RequestBody TopUpRequest topUpRequest) {
        try {
            TopUp topUp = transactionService.createTopUp(
                    topUpRequest.getUserId(),
                    topUpRequest.getAmount()
            );
            TransactionResponse response = mapToTransactionResponse(topUp);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/topup/user/{userId}")
    public ResponseEntity<?> getTopUpsByUser(@PathVariable UUID userId) {
        try {
            List<TopUp> topUps = transactionService.getTopUpsByUser(userId);
            List<TransactionResponse> responses = topUps.stream()
                    .map(this::mapToTransactionResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

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