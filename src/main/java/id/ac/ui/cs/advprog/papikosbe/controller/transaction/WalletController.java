package id.ac.ui.cs.advprog.papikosbe.controller.transaction;

import id.ac.ui.cs.advprog.papikosbe.dto.WalletRequest;
import id.ac.ui.cs.advprog.papikosbe.dto.WalletResponse;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Wallet;
import id.ac.ui.cs.advprog.papikosbe.model.user.Tenant;
import id.ac.ui.cs.advprog.papikosbe.model.user.User;
import id.ac.ui.cs.advprog.papikosbe.service.transaction.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping
    public ResponseEntity<WalletResponse> createWallet(@RequestBody Map<String, Object> payload) {
        UUID userId = UUID.fromString(payload.get("userId").toString());
        Wallet wallet = walletService.create(userId);
        return ResponseEntity.ok(mapToResponse(wallet));
    }

    @GetMapping
    public ResponseEntity<List<WalletResponse>> findAllWallets() {
        List<Wallet> wallets = walletService.findAll();
        List<WalletResponse> responses = wallets.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WalletResponse> findWalletById(@PathVariable UUID id) {
        Wallet wallet = walletService.findById(id);
        if (wallet == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mapToResponse(wallet));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WalletResponse> editWallet(@PathVariable UUID id, @RequestBody WalletRequest request) {
        User user = new Tenant();
        user.setId(request.getUserId());

        Wallet wallet = new Wallet(user, request.getBalance());
        wallet.setId(request.getId());

        Wallet updated = walletService.edit(id, wallet);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mapToResponse(updated));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWallet(@PathVariable UUID id) {
        walletService.delete(id);
        return ResponseEntity.ok().build();
    }

    private WalletResponse mapToResponse(Wallet wallet) {
        return new WalletResponse(
                wallet.getId(),
                wallet.getBalance(),
                wallet.getUser() != null ? wallet.getUser().getId() : null
        );
    }
}

