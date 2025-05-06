// src/test/java/id/ac/ui/cs/advprog/papikosbe/security/JwtTokenProviderTest.java
package id.ac.ui.cs.advprog.papikosbe.security;

import id.ac.ui.cs.advprog.papikosbe.model.user.Admin;
import id.ac.ui.cs.advprog.papikosbe.model.user.Tenant;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

import java.lang.reflect.Method;

class JwtTokenProviderTest {
    private JwtTokenProvider jwtProvider;

    @BeforeEach
    void setUp() throws Exception {
        // gunakan key minimal 256-bit (32 karakter) untuk HMAC-SHA256
        String testSecret = "testsecretkeytestsecretkeytestsec";
        long testValidity = 3600_000L;  // 1 jam
        jwtProvider = new JwtTokenProvider(testSecret, testValidity);

        // inisialisasi signingKey dari secret
        // init() di JwtTokenProvider sebaiknya public,
        // atau panggil via reflection jika masih private:
        Method init = JwtTokenProvider.class.getDeclaredMethod("init");
        init.setAccessible(true);
        init.invoke(jwtProvider);
    }



    @Test
    void testCreateTokenGeneratesNonNullJwt() {
        Tenant user = Tenant.builder()
                .email("tenant@example.com")
                .password("pwd")
                .build();

        String token = jwtProvider.createToken(user);

        assertNotNull(token, "createToken should return a non-null JWT");
        // JWT is three Base64‐encoded parts separated by dots
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT should consist of three parts");
    }

    @Test
    void testCreateTokenDifferentForDifferentUsers() {
        Admin a1 = Admin.builder()
                .email("a1@example.com")
                .password("pw")
                .build();
        Admin a2 = Admin.builder()
                .email("a2@example.com")
                .password("pw")
                .build();

        String t1 = jwtProvider.createToken(a1);
        String t2 = jwtProvider.createToken(a2);

        assertNotEquals(t1, t2, "Tokens for different users should differ");
    }

    @Test
    void testCreateTokenThrowsOnNullUser() {
        assertThrows(NullPointerException.class,
                () -> jwtProvider.createToken(null),
                "createToken should throw NPE when passed null");
    }

    @Test
    void testInvalidateDoesNotThrow() {
        // invalidation strategy to be implemented later
        assertDoesNotThrow(() -> jwtProvider.invalidate("any.token.here"));
    }
}
