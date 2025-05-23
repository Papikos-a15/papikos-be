package id.ac.ui.cs.advprog.papikosbe.util;

import id.ac.ui.cs.advprog.papikosbe.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuthenticationUtils {

    private final UserRepository userRepository;

    @Autowired
    public AuthenticationUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Extract user ID from the authentication object by looking up the email
     * 
     * @param authentication The Spring Security authentication object
     * @return The UUID of the authenticated user
     * @throws IllegalStateException if authentication is null or user not found
     */
    public UUID getUserIdFromAuth(Authentication authentication) {
        if (authentication == null) {
            throw new IllegalStateException("Authentication is required");
        }

        String email = authentication.getName();
        
        return userRepository.findByEmail(email)
            .map(user -> user.getId())
            .orElseThrow(() -> new IllegalStateException("User not found: " + email));
    }
}