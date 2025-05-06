// src/main/java/id/ac/ui/cs/advprog/papikosbe/controller/user/RegisterController.java
package id.ac.ui.cs.advprog.papikosbe.controller.user;

import id.ac.ui.cs.advprog.papikosbe.controller.user.dto.ApiError;
import id.ac.ui.cs.advprog.papikosbe.controller.user.dto.LoginRequest;
import id.ac.ui.cs.advprog.papikosbe.model.user.Owner;
import id.ac.ui.cs.advprog.papikosbe.model.user.Tenant;
import id.ac.ui.cs.advprog.papikosbe.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/register")
@RequiredArgsConstructor
public class RegisterController {

    private final UserService userService;

    /* ----------- SKELETON / placeholder ----------- */

    @PostMapping("/tenant")
    public ResponseEntity<Tenant> registerTenant(@RequestBody LoginRequest body) {
        // TODO: implementasi supaya test hijau
        return ResponseEntity.status(501).build(); // 501 Not Implemented
    }

    @PostMapping("/owner")
    public ResponseEntity<Owner> registerOwner(@RequestBody LoginRequest body) {
        // TODO: implementasi supaya test hijau
        return ResponseEntity.status(501).build();
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleDuplicate(Exception ex) {
        // TODO: mapping DuplicateEmailException -> 409
        return ResponseEntity.status(501).body(new ApiError("not implemented"));
    }
}
