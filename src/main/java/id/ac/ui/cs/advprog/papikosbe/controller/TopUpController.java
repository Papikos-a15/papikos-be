package id.ac.ui.cs.advprog.papikosbe.controller;

import id.ac.ui.cs.advprog.papikosbe.model.TopUp;
import id.ac.ui.cs.advprog.papikosbe.service.transaction.TopUpService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/topups")
public class TopUpController {

    private final TopUpService topUpService;

    public TopUpController(TopUpService topUpService) {
        this.topUpService = topUpService;
    }

    @PostMapping
    public ResponseEntity<TopUp> createTopUp(@RequestBody CreateTopUpRequest request) {
        TopUp created = topUpService.createTopUp(request.userId(), request.amount());
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<TopUp>> getAllTopUps() {
        return ResponseEntity.ok(topUpService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TopUp> getTopUpById(@PathVariable UUID id) {
        TopUp topUp = topUpService.findById(id);
        return topUp != null ? ResponseEntity.ok(topUp) : ResponseEntity.notFound().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TopUp>> getTopUpsByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(topUpService.findByUserId(userId));
    }

    // DTO untuk permintaan top up
    public record CreateTopUpRequest(UUID userId, BigDecimal amount) {}
}
