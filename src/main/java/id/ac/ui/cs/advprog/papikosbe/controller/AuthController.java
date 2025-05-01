package id.ac.ui.cs.advprog.papikosbe.controller;

import id.ac.ui.cs.advprog.papikosbe.controller.dto.LoginRequest;
import id.ac.ui.cs.advprog.papikosbe.controller.dto.LoginResponse;
import id.ac.ui.cs.advprog.papikosbe.controller.dto.RegisterRequest;
import id.ac.ui.cs.advprog.papikosbe.model.User;
import id.ac.ui.cs.advprog.papikosbe.security.JwtTokenUtil;
import id.ac.ui.cs.advprog.papikosbe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        User user = userService.registerUser(
                request.getFullName(),
                request.getPhone(),
                request.getEmail(),
                request.getPassword(),
                request.getRole()
        );
        return ResponseEntity.status(201).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user = userService.authenticate(request.getEmail(), request.getPassword());
        String token = jwtTokenUtil.generateToken(user.getEmail(), user.getRole().name());
        return ResponseEntity.ok(new LoginResponse(token));
    }
}