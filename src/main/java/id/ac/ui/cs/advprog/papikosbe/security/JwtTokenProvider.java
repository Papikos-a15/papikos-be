// src/main/java/id/ac/ui/cs/advprog/papikosbe/security/JwtTokenProvider.java
package id.ac.ui.cs.advprog.papikosbe.security;

import id.ac.ui.cs.advprog.papikosbe.model.user.Owner;
import id.ac.ui.cs.advprog.papikosbe.model.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List;
import java.util.Collection;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtTokenProvider {

    private final String secret;
    private final long validityInMs;
    private Key signingKey;
    private final Set<String> invalidatedTokens = ConcurrentHashMap.newKeySet();

    public JwtTokenProvider(
            @Value("${jwt.secret:defaultSecretKeyChangeMe1234567890}") String secret,
            @Value("${jwt.validity:3600000}") long validityInMs
    ) {
        this.secret = secret;
        this.validityInMs = validityInMs;
    }

    @PostConstruct
    private void init() {
        // Use the raw secret bytes; in production you’d want at least 256 bits of entropy.
        signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generate a JWT for the given user.
     *
     * @param user the authenticated user (must not be null)
     * @return a signed JWT string
     * @throws NullPointerException if user is null
     */
    public String createToken(User user) {
        Objects.requireNonNull(user, "user must not be null");
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityInMs);

        var builder = Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole().name())
                .claim("userId", user.getId().toString())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(signingKey, SignatureAlgorithm.HS256);

        // only Owners have an approval flag
        if (user instanceof Owner) {
            builder.claim("approved", ((Owner) user).isApproved());
        }

        return builder.compact();
    }

    /**
     * Invalidate the given token (e.g. add it to a blacklist).
     *
     * @param token the JWT to invalidate (must not be null)
     * @throws NullPointerException if token is null
     */
    public void invalidate(String token) {
        Objects.requireNonNull(token, "token must not be null");
        invalidatedTokens.add(token);
    }

    /**
     * Check if a token is valid (not expired, signature ok, and not explicitly invalidated).
     *
     * @param token the JWT to validate
     * @return true if valid, false otherwise
     */
    public boolean validate(String token) {
        Objects.requireNonNull(token, "token must not be null");
        if (invalidatedTokens.contains(token)) {
            return false;
        }
        try {
            Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String email = claims.getSubject();
        String role = claims.get("role", String.class);
        Collection<SimpleGrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority("ROLE_" + role));

        var auth = new UsernamePasswordAuthenticationToken(email, null, authorities);
        // simpan flag approved (jika ada) ke details
        if (claims.containsKey("approved")) {
            auth.setDetails(claims.get("approved", Boolean.class));
        }
        return auth;
    }
}
