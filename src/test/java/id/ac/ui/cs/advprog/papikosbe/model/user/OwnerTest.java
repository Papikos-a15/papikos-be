package id.ac.ui.cs.advprog.papikosbe.model.user;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OwnerTest {

    @Test
    void testCreateOwnerSuccess() {
        Owner owner = Owner.builder()
                .email("owner@example.com")
                .password("ownerpass")
                .build();

        assertEquals("owner@example.com", owner.getEmail());
        assertEquals("ownerpass",         owner.getPassword());
    }

    @Test
    void testOwnerDefaultApprovedFalse() {
        Owner owner = Owner.builder()
                .email("owner@example.com")
                .password("ownerpass")
                .build();

        assertFalse(owner.isApproved());
    }

    @Test
    void testSetApprovedTrue() {
        Owner owner = Owner.builder()
                .email("o@k.com")
                .password("pwd")
                .build();
        owner.setApproved(true);

        assertTrue(owner.isApproved());
    }

    @Test
    void testBuilderThrowsOnNullEmail() {
        assertThrows(NullPointerException.class, () ->
                Owner.builder()
                        .email(null)
                        .password("pwd")
                        .build()
        );
    }

    @Test
    void testBuilderThrowsOnNullPassword() {
        assertThrows(NullPointerException.class, () ->
                Owner.builder()
                        .email("a@b.com")
                        .password(null)
                        .build()
        );
    }

    @Test
    void testGettersAndSetters() {
        Owner owner = new Owner();
        owner.setId(99L);
        owner.setEmail("set@owner.com");
        owner.setPassword("setowner");
        owner.setApproved(true);

        assertAll("owner setters/getters",
                () -> assertEquals(99L,                owner.getId()),
                () -> assertEquals("set@owner.com",   owner.getEmail()),
                () -> assertEquals("setowner",        owner.getPassword()),
                () -> assertTrue(owner.isApproved())
        );
    }
}