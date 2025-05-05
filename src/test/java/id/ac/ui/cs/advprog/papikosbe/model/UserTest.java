package id.ac.ui.cs.advprog.papikosbe.model;

import org.junit.jupiter.api.Test;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.*;
import id.ac.ui.cs.advprog.papikosbe.enums.Role;


class UserTest {

    @Test
    void shouldBeAnnotatedWithEntity() {
        assertTrue(User.class.isAnnotationPresent(Entity.class),
                "User class must be annotated with @Entity");
    }

    @Test
    void idFieldShouldHaveIdAndGeneratedValueAnnotations() throws NoSuchFieldException {
        Field idField = User.class.getDeclaredField("id");
        assertTrue(idField.isAnnotationPresent(Id.class),
                "Field 'id' must be annotated with @Id");
        assertTrue(idField.isAnnotationPresent(GeneratedValue.class),
                "Field 'id' must be annotated with @GeneratedValue");
    }

    @Test
    void defaultIdShouldBeNull() {
        User u = new User();
        assertNull(u.getId(), "New User.id should be null before persisting");
    }

    @Test
    void defaultIsApprovedShouldBeFalse() {
        User u = new User();
        assertFalse(u.isApproved(), "New User.isApproved should default to false");
    }

    @Test
    void gettersAndSettersShouldWork() {
        User u = new User();
        u.setEmail("test@example.com");
        u.setPassword("secret");
        u.setRole(Role.TENANT);

        assertEquals("test@example.com", u.getEmail());
        assertEquals("secret", u.getPassword());
        assertEquals(Role.TENANT, u.getRole());
    }
    @Test
    void testBuilderDefaultsApprovalFalse() {
        User u = User.builder()
                .email("owner@kos.com")
                .password("ownerpass")
                .role(Role.OWNER)
                .build();
        assertFalse(u.isApproved());
    }

    @Test
    void testParameterizedConstructor() {
        User u = new User("admin@site.com", "adminpass", Role.ADMIN);
        assertAll(
                () -> assertEquals("admin@site.com", u.getEmail()),
                () -> assertEquals("adminpass", u.getPassword()),
                () -> assertEquals(Role.ADMIN, u.getRole()),
                () -> assertFalse(u.isApproved())
        );
    }

    // Unhappy-path tests

    @Test
    void testConstructorThrowsOnNullEmail() {
        assertThrows(NullPointerException.class, () -> new User(null, "pwd", Role.TENANT));
    }

    @Test
    void testConstructorThrowsOnNullPassword() {
        assertThrows(NullPointerException.class, () -> new User("e@f.com", null, Role.OWNER));
    }

    @Test
    void testConstructorThrowsOnNullRole() {
        assertThrows(NullPointerException.class, () -> new User("x@y.com", "pwd", null));
    }

    @Test
    void testBuilderThrowsOnNullEmail() {
        assertThrows(NullPointerException.class, () ->
                User.builder()
                        .email(null)
                        .password("p")
                        .role(Role.TENANT)
                        .build()
        );
    }

    @Test
    void testBuilderThrowsOnNullPassword() {
        assertThrows(NullPointerException.class, () ->
                User.builder()
                        .email("a@b.com")
                        .password(null)
                        .role(Role.OWNER)
                        .build()
        );
    }

    @Test
    void testBuilderThrowsOnNullRole() {
        assertThrows(NullPointerException.class, () ->
                User.builder()
                        .email("a@b.com")
                        .password("pwd")
                        .role(null)
                        .build()
        );
    }
}
