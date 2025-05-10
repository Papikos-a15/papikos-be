// src/main/java/id/ac/ui/cs/advprog/papikosbe/service/AuthService.java
package id.ac.ui.cs.advprog.papikosbe.service.user;

public interface AuthService {
    /**
     * Verify credentials for the given email/password, then
     * generate and return a JWT token.
     *
     * @param email the user’s email
     * @param rawPassword the plaintext password to check
     * @return a JWT token if login is successful
     */
    String login(String email, String rawPassword);

    /**
     * Invalidate the given token (e.g. add to blacklist or revoke).
     *
     * @param token the JWT to invalidate
     */
    void logout(String token);
}
