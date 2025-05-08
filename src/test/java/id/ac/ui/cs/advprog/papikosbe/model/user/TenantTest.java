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

    @Test
    void testGettersAndSetters() {
        Tenant tenant = new Tenant();
        tenant.setId(7L);
        tenant.setEmail("set@tenant.com");
        tenant.setPassword("setpass");

        assertAll("tenant setters/getters",
                () -> assertEquals(7L,                  tenant.getId()),
                () -> assertEquals("set@tenant.com",   tenant.getEmail()),
                () -> assertEquals("setpass",          tenant.getPassword())
        );
    }
}
