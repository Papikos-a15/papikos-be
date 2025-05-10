// src/main/java/id/ac/ui/cs/advprog/papikosbe/service/impl/AuthServiceImpl.java
package id.ac.ui.cs.advprog.papikosbe.service.user;

import id.ac.ui.cs.advprog.papikosbe.exception.BadCredentialsException;
import id.ac.ui.cs.advprog.papikosbe.model.user.User;
import id.ac.ui.cs.advprog.papikosbe.repository.user.UserRepository;
import id.ac.ui.cs.advprog.papikosbe.security.JwtTokenProvider;
import id.ac.ui.cs.advprog.papikosbe.service.user.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtProvider;

    @Override
    public String login(String email, String rawPassword) {
        // 1. Cari user berdasarkan email
        User user = userRepo.findByEmail(email)
                .orElseThrow(BadCredentialsException::new);

        // 2. Cocokkan password
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BadCredentialsException();
        }

        // 3. Buat & kembalikan JWT
        return jwtProvider.createToken(user);
    }

    @Override
    public void logout(String token) {
        // Strategi sederhana: blacklist token
        jwtProvider.invalidate(token);
    }

    @Override
    public UUID getUserIdByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"))
                .getId();
    }

    @Override
    public String getUserRoleByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"))
                .getRole().name();
    }
}