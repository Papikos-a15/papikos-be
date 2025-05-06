// src/main/java/id/ac/ui/cs/advprog/papikosbe/security/JwtTokenProvider.java
package id.ac.ui.cs.advprog.papikosbe.security;

import id.ac.ui.cs.advprog.papikosbe.model.user.User;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    /**
     * Generate a JWT for the given user.
     *
     * @param user the authenticated user
     * @return a signed JWT string
     */
    public String createToken(User user) {
        // TODO: implement JWT creation (e.g. build claims with user ID, role, approval flag)
        throw new UnsupportedOperationException("createToken not implemented");
    }

    /**
     * Invalidate the given token (e.g. add it to a blacklist or revoke it).
     *
     * @param token the JWT to invalidate
     */
    public void invalidate(String token) {
        // TODO: implement token revocation strategy
        throw new UnsupportedOperationException("invalidate not implemented");
    }
}
