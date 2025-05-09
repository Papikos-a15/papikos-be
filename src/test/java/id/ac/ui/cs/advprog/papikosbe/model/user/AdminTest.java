package id.ac.ui.cs.advprog.papikosbe.model.user;

import org.junit.jupiter.api.Test;

import java.util.UUID;

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

    @Test
    void testGettersAndSetters() {
        UUID id = UUID.randomUUID();
        Admin admin = new Admin();
        admin.setId(id);
        admin.setEmail("setter@admin.com");
        admin.setPassword("setterpass");

        assertAll("admin setters/getters",
                () -> assertEquals(id, admin.getId()),
                () -> assertEquals("setter@admin.com", admin.getEmail()),
                () -> assertEquals("setterpass",       admin.getPassword())
        );
    }
}