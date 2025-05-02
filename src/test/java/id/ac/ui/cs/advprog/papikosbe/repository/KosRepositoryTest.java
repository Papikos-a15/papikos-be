package id.ac.ui.cs.advprog.papikosbe.repository;

import id.ac.ui.cs.advprog.papikosbe.model.Kos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class KosRepositoryTest {

    private KosRepository kosRepository;
    // A list of sample Kos objects to be used in tests.
    private List<Kos> kosList;

    @BeforeEach
    public void setUp() {
        // Instantiate your in-memory repository.
        kosRepository = new KosRepository();
        // Prepare some sample Kos entries.
        kosList = new ArrayList<>();

        Kos kos1 = new Kos();
        kos1.setName("Kos1");
        kos1.setAddress("AlamatKos1");
        kos1.setDescription("DeskripsiKos1");
        kos1.setPrice(50000.00);
        kosList.add(kos1);

        Kos kos2 = new Kos();
        kos2.setName("Kos2");
        kos2.setAddress("AlamatKos2");
        kos2.setDescription("DeskripsiKos2");
        kos2.setPrice(50000.00);
        kosList.add(kos2);
    }

    @Test
    public void testAddKos() {
        // Add the first Kos from our sample list.
        Kos kos = kosList.getFirst();
        Kos savedKos = kosRepository.save(kos);

        // Verify that an ID was assigned and that the stored values are correct.
        assertNotNull(savedKos.getId(), "The saved Kos should have a non-null ID");
        assertEquals("Kos1", savedKos.getName());
        assertEquals("AlamatKos1", savedKos.getAddress());
        assertEquals("DeskripsiKos1", savedKos.getDescription());
        assertEquals(50000.00, savedKos.getPrice());
    }

    @Test
    public void testGetKosById() {
        // Save a Kos entry.
        Kos kos = kosList.get(1);
        Kos savedKos = kosRepository.save(kos);
        String id = savedKos.getId();

        // Retrieve the Kos by its ID.
        Kos foundKos = kosRepository.getKosById(id);
        assertNotNull(foundKos, "Kos should be found by its ID");
        assertEquals("Kos2", foundKos.getName());
        assertEquals("AlamatKos2", foundKos.getAddress());
        assertEquals("DeskripsiKos2", foundKos.getDescription());
        assertEquals(50000.00, foundKos.getPrice());
    }

    @Test
    public void testGetAllKos() {
        // Save both Kos entries.
        kosRepository.save(kosList.get(0));
        kosRepository.save(kosList.get(1));

        // Retrieve all Kos entries.
        List<Kos> allKos = kosRepository.getAllKos();
        assertEquals(2, allKos.size(), "There should be exactly two Kos entries stored");
    }

    @Test
    public void testUpdateKos() {
        // Save a Kos entry.
        Kos kos = kosList.getFirst();
        Kos savedKos = kosRepository.save(kos);
        String id = savedKos.getId();

        // Create an updated object.
        Kos updatedKos = new Kos();
        updatedKos.setName("UpdatedKos");
        updatedKos.setAddress("UpdatedAlamat");
        updatedKos.setDescription("UpdatedDeskripsi");
        updatedKos.setPrice(75000.00);

        // Update the Kos entry.
        Kos resultKos = kosRepository.updateKos(id, updatedKos);

        // Verify that the updated fields match.
        assertNotNull(resultKos, "Updated Kos should not be null");
        assertEquals(id, resultKos.getId());
        assertEquals("UpdatedKos", resultKos.getName());
        assertEquals("UpdatedAlamat", resultKos.getAddress());
        assertEquals("UpdatedDeskripsi", resultKos.getDescription());
        assertEquals(75000.00, resultKos.getPrice());
    }

    @Test
    public void testDeleteKos() {
        // Save a Kos entry.
        Kos kos = kosList.getFirst();
        Kos savedKos = kosRepository.save(kos);
        String id = savedKos.getId();

        // Delete the Kos entry.
        boolean deleted = kosRepository.deleteKos(id);
        assertTrue(deleted, "Deletion should return true");

        // Verify that the Kos entry is no longer available.
        Kos resultKos = kosRepository.getKosById(id);
        assertNull(resultKos, "Kos should no longer exist in the repository after deletion");
    }
}