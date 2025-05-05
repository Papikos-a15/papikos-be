package id.ac.ui.cs.advprog.model;

import org.junit.jupiter.api.Test;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.*;

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
}
