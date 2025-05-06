// src/main/java/id/ac/ui/cs/advprog/papikosbe/controller/user/OwnerController.java
package id.ac.ui.cs.advprog.papikosbe.controller.user;

import id.ac.ui.cs.advprog.papikosbe.controller.dto.ApiError;
import id.ac.ui.cs.advprog.papikosbe.model.user.Owner;
import id.ac.ui.cs.advprog.papikosbe.service.user.OwnerService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/owners")
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerService ownerService;

    /* ---------- APPROVE OWNER ---------- */
    @PatchMapping("/{id}/approve")
    public ResponseEntity<Owner> approve(@PathVariable Long id) {
        Owner approved = ownerService.approve(id);
        return ResponseEntity.ok(approved);
    }

    /* ---------- ERROR HANDLER ---------- */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound() {
        return ResponseEntity.status(404).body(new ApiError("Owner not found"));
    }
}
