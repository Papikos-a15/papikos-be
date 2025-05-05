package id.ac.ui.cs.advprog.papikosbe.model.user;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TenantTest {

    @Test
    void testCreateTenantSuccess() {
        Tenant tenant = Tenant.builder()
                .email("tenant@example.com")
                .password("tenantpass")
                .build();

        assertEquals("tenant@example.com", tenant.getEmail());
        assertEquals("tenantpass",         tenant.getPassword());
    }

    @Test
    void testBuilderThrowsOnNullEmail() {
        assertThrows(NullPointerException.class, () ->
                Tenant.builder()
                        .email(null)
                        .password("pass")
                        .build()
        );
    }

    @Test
    void testBuilderThrowsOnNullPassword() {
        assertThrows(NullPointerException.class, () ->
                Tenant.builder()
                        .email("a@b.com")
                        .password(null)
                        .build()
        );
    }
}
