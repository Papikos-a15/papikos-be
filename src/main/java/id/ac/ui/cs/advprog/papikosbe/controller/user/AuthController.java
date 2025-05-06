// src/main/java/id/ac/ui/cs/advprog/papikosbe/web/AuthController.java
package id.ac.ui.cs.advprog.papikosbe.controller.user;

import id.ac.ui.cs.advprog.papikosbe.exception.BadCredentialsException;
import id.ac.ui.cs.advprog.papikosbe.security.JwtTokenProvider;
import id.ac.ui.cs.advprog.papikosbe.service.user.AuthService;
import id.ac.ui.cs.advprog.papikosbe.controller.dto.ApiError;
import id.ac.ui.cs.advprog.papikosbe.controller.dto.LoginRequest;
import id.ac.ui.cs.advprog.papikosbe.controller.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtProvider;   // jika nanti dibutuhkan

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest req) {
        // TODO: panggil authService.login dan kembalikan ResponseEntity.ok(new TokenResponse(token))
        return ResponseEntity.ok(new TokenResponse("dummy")); // placeholder
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String bearer) {
        // TODO: ekstrak token (tanpa "Bearer "), panggil authService.logout(token)
        return ResponseEntity.ok().build(); // placeholder
    }

    /* ---- Error mapping ---- */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCreds(BadCredentialsException ex) {
        // TODO: sesuaikan status 401
        return ResponseEntity.status(401).body(new ApiError(ex.getMessage()));
    }
}
