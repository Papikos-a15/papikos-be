package id.ac.ui.cs.advprog.papikosbe.repository.kos;

import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class KosRepositoryTest {

    @Autowired
    private KosRepository kosRepository;
    // A list of sample Kos objects to be used in tests.
    private List<Kos> kosList;

    @BeforeEach
    public void setUp() {
        // Prepare some sample Kos entries.
        kosList = new ArrayList<>();

        Kos kos1 = new Kos();
        kos1.setId(UUID.randomUUID());
        kos1.setTenantId(UUID.randomUUID());
        kos1.setName("Kos1");
        kos1.setAddress("AlamatKos1");
        kos1.setDescription("DeskripsiKos1");
        kos1.setPrice(50000.00);
        kosList.add(kos1);

        Kos kos2 = new Kos();
        kos2.setOwnerId(UUID.randomUUID());
        kos2.setTenantId(UUID.randomUUID());
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
        UUID id = savedKos.getId();

        // Retrieve the Kos by its ID.
        Optional<Kos> foundKos = kosRepository.findById(id);
        if(foundKos.isPresent()) {
            assertEquals("Kos2", foundKos.get().getName());
            assertEquals("AlamatKos2", foundKos.get().getAddress());
            assertEquals("DeskripsiKos2", foundKos.get().getDescription());
            assertEquals(50000.00, foundKos.get().getPrice());
        }
    }

    @Test
    public void testGetAllKos() {
        // Save both Kos entries.
        kosRepository.save(kosList.get(0));
        kosRepository.save(kosList.get(1));

        // Retrieve all Kos entries.
        List<Kos> allKos = kosRepository.findAll();
        assertEquals(2, allKos.size(), "There should be exactly two Kos entries stored");
    }

    @Test
    public void testUpdateKos() {
        // Save a Kos entry.
        Kos kos = kosList.getFirst();
        Kos savedKos = kosRepository.save(kos);
        UUID id = savedKos.getId();

        Optional<Kos> foundKos = kosRepository.findById(id);
        if(foundKos.isPresent()) {
            foundKos.get().setName("UpdatedKos");
            foundKos.get().setAddress("UpdatedAlamat");
            foundKos.get().setDescription("UpdatedDeskripsi");
            foundKos.get().setPrice(75000.00);

            // Verify that the updated fields match.
            assertEquals(id, foundKos.get().getId());
            assertEquals("UpdatedKos", foundKos.get().getName());
            assertEquals("UpdatedAlamat", foundKos.get().getAddress());
            assertEquals("UpdatedDeskripsi", foundKos.get().getDescription());
            assertEquals(75000.00, foundKos.get().getPrice());
        }

    }

    @Test
    public void testDeleteKos() {
        // Save a Kos entry.
        Kos kos = kosList.getFirst();
        Kos savedKos = kosRepository.save(kos);
        UUID id = savedKos.getId();

        // Delete the Kos entry.
        kosRepository.deleteById(id);

        // Verify that the Kos entry is no longer available.
        Optional<Kos> resultKos = kosRepository.findById(id);
        assertNotNull(resultKos, "Kos should no longer exist in the repository after deletion");
    }
}