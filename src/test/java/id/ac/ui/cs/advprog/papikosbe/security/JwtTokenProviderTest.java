// src/test/java/id/ac/ui/cs/advprog/papikosbe/security/JwtTokenProviderTest.java
package id.ac.ui.cs.advprog.papikosbe.security;

import id.ac.ui.cs.advprog.papikosbe.model.user.Admin;
import id.ac.ui.cs.advprog.papikosbe.model.user.Owner;
import id.ac.ui.cs.advprog.papikosbe.model.user.Tenant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {
    private JwtTokenProvider jwtProvider;

    @BeforeEach
    void setUp() throws Exception {
        // gunakan key minimal 256-bit (32 karakter) untuk HMAC-SHA256
        String testSecret = "testsecretkeytestsecretkeytestsec";
        long testValidity = 3600_000L;  // 1 jam
        jwtProvider = new JwtTokenProvider(testSecret, testValidity);

        // inisialisasi signingKey dari secret
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
        user.setId(UUID.randomUUID());

        String token = jwtProvider.createToken(user);

        assertNotNull(token, "createToken should return a non-null JWT");
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT should consist of three parts");
    }

    @Test
    void testCreateTokenDifferentForDifferentUsers() {
        Admin a1 = Admin.builder().email("a1@example.com").password("pw").build();
        Admin a2 = Admin.builder().email("a2@example.com").password("pw").build();
        a1.setId(UUID.randomUUID());
        a2.setId(UUID.randomUUID());

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
    void testValidateValidAndInvalidTokens() {
        Tenant t = Tenant.builder().email("t@mail.com").password("pw").build();
        t.setId(UUID.randomUUID());

        String token = jwtProvider.createToken(t);
        assertTrue(jwtProvider.validate(token), "new token should be valid");

        assertFalse(jwtProvider.validate("not.a.jwt"), "random string is not a valid JWT");
    }

    @Test
    void testInvalidateRemovesValidity() {
        Tenant t = Tenant.builder().email("x@mail.com").password("pw").build();
        t.setId(UUID.randomUUID());

        String token = jwtProvider.createToken(t);
        assertTrue(jwtProvider.validate(token));

        jwtProvider.invalidate(token);
        assertFalse(jwtProvider.validate(token), "invalidated token must not validate");
    }

    @Test
    void testValidateThrowsOnNull() {
        assertThrows(NullPointerException.class,
                () -> jwtProvider.validate(null),
                "validate(null) should throw NPE");
    }

    @Test
    void testInvalidateThrowsOnNull() {
        assertThrows(NullPointerException.class,
                () -> jwtProvider.invalidate(null),
                "invalidate(null) should throw NPE");
    }

    @Test
    void testGetAuthenticationForTenant() {
        Tenant t = Tenant.builder()
                .email("tenant@x.com")
                .password("pw")
                .build();
        t.setId(UUID.randomUUID());

        String token = jwtProvider.createToken(t);
        assertTrue(jwtProvider.validate(token));

        Authentication auth = jwtProvider.getAuthentication(token);
        assertNotNull(auth, "getAuthentication should not return null");
        assertEquals("tenant@x.com", auth.getName());
        assertTrue(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_TENANT")));
    }

    @Test
    void testGetAuthenticationForOwnerIncludesApproval() {
        Owner o = Owner.builder()
                .email("owner@x.com")
                .password("pw")
                .build();  // approved defaults to false
        o.setId(UUID.randomUUID());

        String token = jwtProvider.createToken(o);
        assertTrue(jwtProvider.validate(token));

        Authentication auth = jwtProvider.getAuthentication(token);
        assertNotNull(auth);
        assertEquals("owner@x.com", auth.getName());
        assertTrue(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_OWNER")));

        // jika Anda simpan klaim "approved" sebagai detail:
        Object approvedClaim = auth.getDetails();
        // contoh: provider.setDetails(claims.get("approved"))
        assertNotNull(approvedClaim, "Authentication details should include approval flag");
        assertEquals(false, approvedClaim);
    }
}
