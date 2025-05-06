// src/main/java/id/ac/ui/cs/advprog/papikosbe/service/impl/AuthServiceImpl.java
package id.ac.ui.cs.advprog.papikosbe.service.user;

import id.ac.ui.cs.advprog.papikosbe.service.user.AuthService;
import id.ac.ui.cs.advprog.papikosbe.repository.user.UserRepository;
import id.ac.ui.cs.advprog.papikosbe.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtProvider;

    public AuthServiceImpl(UserRepository userRepo,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtProvider) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public String login(String email, String rawPassword) {
        // TODO: lookup user, verify password, generate token
        throw new UnsupportedOperationException("login not implemented");
    }

    @Override
    public void logout(String token) {
        // TODO: invalidate token
        throw new UnsupportedOperationException("logout not implemented");
    }
}
