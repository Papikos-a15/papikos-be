package id.ac.ui.cs.advprog.papikosbe.model.kos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class KosTest {

    @Test
    public void testDefaultConstructorAndSetters() {
        // Create a new instance using the default constructor
        Kos kos = new Kos();

        // Initially, id should be null, and other fields should be null.
        assertNull(kos.getId(), "ID should be null before being set");
        assertNull(kos.getName(), "Name should be null initially");
        assertNull(kos.getAddress(), "Address should be null initially");
        assertNull(kos.getDescription(), "Description should be null initially");
        assertNull(kos.getPrice(), "Price should be null initially");

        // Use setters to assign values
        kos.setName("Kos Example");
        kos.setAddress("Example Address");
        kos.setDescription("Example Description");
        kos.setPrice(75000.0);

        // Verify getters return the expected values.
        assertEquals("Kos Example", kos.getName(), "Name should match the value set");
        assertEquals("Example Address", kos.getAddress(), "Address should match the value set");
        assertEquals("Example Description", kos.getDescription(), "Description should match the value set");
        assertEquals(75000.0, kos.getPrice(), "Price should match the value set");
    }

    @Test
    public void testParameterizedConstructor() {
        // Create a new instance using the parameterized constructor.
        Kos kos = new Kos("1234567890", "Kos Param", "Param Address", "Param Description", 90000.0);

        // The fields should be set as provided
        assertNotNull(kos.getId(), "ID should be not null when explicitly set");
        assertEquals("Kos Param", kos.getName(), "Name should match constructor argument");
        assertEquals("Param Address", kos.getAddress(), "Address should match constructor argument");
        assertEquals("Param Description", kos.getDescription(), "Description should match constructor argument");
        assertEquals(90000.0, kos.getPrice(), "Price should match constructor argument");
    }
}
