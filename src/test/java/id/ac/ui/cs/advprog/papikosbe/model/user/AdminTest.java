package id.ac.ui.cs.advprog.papikosbe.model.user;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AdminTest {

    @Test
    void testCreateAdminSuccess() {
        Admin admin = Admin.builder()
                .email("admin@example.com")
                .password("adminpass")
                .build();

        assertEquals("admin@example.com", admin.getEmail());
        assertEquals("adminpass",       admin.getPassword());
    }

    @Test
    void testBuilderThrowsOnNullEmail() {
        assertThrows(NullPointerException.class, () ->
                Admin.builder()
                        .email(null)
                        .password("pass")
                        .build()
        );
    }

    @Test
    void testBuilderThrowsOnNullPassword() {
        assertThrows(NullPointerException.class, () ->
                Admin.builder()
                        .email("a@b.com")
                        .password(null)
                        .build()
        );
    }
}
