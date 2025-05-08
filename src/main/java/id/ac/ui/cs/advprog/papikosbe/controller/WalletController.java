package id.ac.ui.cs.advprog.papikosbe.controller;

import id.ac.ui.cs.advprog.papikosbe.model.Wallet;
import id.ac.ui.cs.advprog.papikosbe.service.transaction.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping
    public ResponseEntity<Wallet> createWallet(@RequestBody Map<String, Object> payload) {
        UUID userId = UUID.fromString(payload.get("userId").toString());
        Wallet wallet = walletService.create(userId);
        System.out.println("WALLET: " + wallet);
        return ResponseEntity.ok(wallet);
    }


    @GetMapping
    public ResponseEntity<List<Wallet>> findAllWallets() {
        List<Wallet> wallets = walletService.findAll();
        return ResponseEntity.ok(wallets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Wallet> findWalletById(@PathVariable UUID id) {
        Wallet wallet = walletService.findById(id);
        if (wallet == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(wallet);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Wallet> editWallet(@PathVariable UUID id, @RequestBody Wallet wallet) {
        Wallet updated = walletService.edit(id, wallet);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWallet(@PathVariable UUID id) {
        walletService.delete(id);
        return ResponseEntity.ok().build();
    }
}
