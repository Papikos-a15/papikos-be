// src/main/java/id/ac/ui/cs/advprog/papikosbe/web/OwnerController.java
package id.ac.ui.cs.advprog.papikosbe.controller.user;

import id.ac.ui.cs.advprog.papikosbe.model.user.Owner;
import id.ac.ui.cs.advprog.papikosbe.service.user.OwnerService;
import id.ac.ui.cs.advprog.papikosbe.controller.dto.ApiError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/owners")
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerService ownerService;

    @PatchMapping("/{id}/approve")
    public ResponseEntity<Owner> approve(@PathVariable Long id) {
        // TODO: panggil ownerService.approve(id) & kembalikan ok(owner)
        return ResponseEntity.ok(new Owner()); // placeholder
    }

    /* ---- Error mapping ---- */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(404).body(new ApiError("Owner not found"));
    }
}
