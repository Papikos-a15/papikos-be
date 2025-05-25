// src/main/java/id/ac/ui/cs/advprog/papikosbe/controller/user/AuthController.java
package id.ac.ui.cs.advprog.papikosbe.controller.user;

import id.ac.ui.cs.advprog.papikosbe.controller.user.dto.ApiError;
import id.ac.ui.cs.advprog.papikosbe.controller.user.dto.LoginRequest;
import id.ac.ui.cs.advprog.papikosbe.controller.user.dto.TokenResponse;
import id.ac.ui.cs.advprog.papikosbe.exception.BadCredentialsException;
import id.ac.ui.cs.advprog.papikosbe.service.user.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /* ---------- LOGIN ---------- */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest body) {
        // Authenticate the user and generate the JWT token
        String token = authService.login(body.email(), body.password());

        // Retrieve the userId (assuming you have a method to get user by email)
        UUID userId = authService.getUserIdByEmail(body.email());

        // Return the token and userId in the response
        return ResponseEntity.ok(new TokenResponse(token, userId));
    }


    /* ---------- LOGOUT ---------- */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String bearer) {

        // Header berbentuk: "Bearer <token>"
        String token = bearer.replaceFirst("(?i)^Bearer\\s+", "");
        authService.logout(token);
        return ResponseEntity.ok().build();
    }

    /* ---------- ERROR HANDLER ---------- */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCred(BadCredentialsException ex) {
        return ResponseEntity.status(401).body(new ApiError(ex.getMessage()));
    }
}
