// src/main/java/id/ac/ui/cs/advprog/papikosbe/controller/user/RegisterController.java
package id.ac.ui.cs.advprog.papikosbe.controller.user;

import id.ac.ui.cs.advprog.papikosbe.controller.user.dto.ApiError;
import id.ac.ui.cs.advprog.papikosbe.controller.user.dto.LoginRequest;
import id.ac.ui.cs.advprog.papikosbe.exception.DuplicateEmailException;
import id.ac.ui.cs.advprog.papikosbe.model.user.Owner;
import id.ac.ui.cs.advprog.papikosbe.model.user.Tenant;
import id.ac.ui.cs.advprog.papikosbe.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/register")
@RequiredArgsConstructor
public class RegisterController {

    private final UserService userService;

    /* ---------- REGISTER TENANT ---------- */
    @PostMapping("/tenant")
    public ResponseEntity<Tenant> registerTenant(@RequestBody LoginRequest body) {
        Tenant tenant = userService.registerTenant(body.email(), body.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(tenant);
    }

    /* ---------- REGISTER OWNER ---------- */
    @PostMapping("/owner")
    public ResponseEntity<Owner> registerOwner(@RequestBody LoginRequest body) {
        Owner owner = userService.registerOwner(body.email(), body.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(owner);
    }

    /* ---------- ERROR HANDLER ---------- */
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiError> handleDuplicate(DuplicateEmailException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiError(ex.getMessage()));
    }
}
