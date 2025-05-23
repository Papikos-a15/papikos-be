package id.ac.ui.cs.advprog.papikosbe.service.kos;

import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import id.ac.ui.cs.advprog.papikosbe.repository.kos.KosRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KosServiceImplTest {

    @Mock
    private KosRepository kosRepository;

    @InjectMocks
    private KosServiceImpl kosService;

    private Kos kos1;
    private Kos kos2;

    @BeforeEach
    public void setUp() {
        kos1 = new Kos();
        kos1.setId(UUID.randomUUID());
        kos1.setName("Kos1");
        kos1.setAddress("AlamatKos1");
        kos1.setDescription("DeskripsiKos1");
        kos1.setMaxCapacity(30);
        kos1.setAvailableRooms(30);
        kos1.setPrice(50000.0);

        kos2 = new Kos();
        kos2.setId(UUID.randomUUID());
        kos2.setName("Kos2");
        kos2.setAddress("AlamatKos2");
        kos2.setDescription("DeskripsiKos2");
        kos2.setMaxCapacity(20);
        kos2.setAvailableRooms(20);
        kos2.setPrice(60000.0);
    }

    @Test
    public void testAddKos() {
        Kos kos = kos1;
        doReturn(kos).when(kosRepository).save(kos);

        Kos addedKos = kosService.addKos(kos1);

        assertNotNull(addedKos, "The added Kos should not be null");
        assertEquals(kos.getId(), addedKos.getId(), "The Kos ID should be the same");
        assertEquals(kos.getName(), addedKos.getName());
        assertEquals(kos.getMaxCapacity(), addedKos.getAvailableRooms(), "Available rooms should be equal to max initially");
        verify(kosRepository, times(1)).save(kos1);
    }

    @Test
    public void testGetAllKos() {
        doReturn(Arrays.asList(kos1, kos2)).when(kosRepository).findAll();

        List<Kos> result = kosService.getAllKos();
        assertNotNull(result, "The returned list should not be null");
        assertEquals(2, result.size(), "There should be two Kos entries");
        verify(kosRepository, times(1)).findAll();
    }

    @Test
    public void testGetKosById() {
        Kos kos = kos1;
        doReturn(Optional.of(kos)).when(kosRepository).findById(kos1.getId());

        Optional<Kos> retrievedKos = kosService.getKosById(kos.getId());

        assertNotNull(retrievedKos, "Kos should be present for the given ID");
        retrievedKos.ifPresent(value -> assertEquals(kos.getName(), value.getName()));
        verify(kosRepository, times(1)).findById(kos.getId());
    }

    @Test
    public void testUpdateKos() {
        Kos kos = kos1;
        Kos updatedKos = kos2;

        Optional<Kos> result = kosService.updateKos(kos.getId(), updatedKos);

        assertNotNull(result, "The updated Kos should not be null");
        if (result.isPresent()) {
            assertEquals(kos.getId(), result.get().getId());
            assertEquals("Kos2", result.get().getName());
            assertEquals("AlamatKos2", result.get().getAddress());
            assertEquals("DeskripsiKos2", result.get().getDescription());
            assertEquals(75000.0, result.get().getPrice());
            verify(kosRepository, times(1)).save(kos);
        }
    }

    @Test
    public void testSubtractAvailableRooms() {
        Kos kos = kos1;
        doReturn(Optional.of(kos)).when(kosRepository).findById(kos1.getId());

        kosService.subtractAvailableRoom(kos.getId());

        Optional<Kos> result = kosService.getKosById(kos.getId());
        assertNotNull(result, "Kos should be present for the given ID");
        if (result.isPresent()) {
            assertEquals(kos.getId(), result.get().getId());
            assertEquals("Kos1", result.get().getName());
            assertEquals("AlamatKos1", result.get().getAddress());
            assertEquals("DeskripsiKos1", result.get().getDescription());
            assertEquals(29, result.get().getAvailableRooms());
            assertEquals(50000.0, result.get().getPrice());
        }
    }

    @Test
    public void testAddAvailableRoom() {
        Kos kos = kos1;
        doReturn(Optional.of(kos)).when(kosRepository).findById(kos1.getId());

        kosService.addAvailableRoom(kos.getId());

        Optional<Kos> result = kosService.getKosById(kos.getId());
        assertNotNull(result, "Kos should be present for the given ID");
        if (result.isPresent()) {
            assertEquals(kos.getId(), result.get().getId());
            assertEquals("Kos1", result.get().getName());
            assertEquals("AlamatKos1", result.get().getAddress());
            assertEquals("DeskripsiKos1", result.get().getDescription());
            assertEquals(31, result.get().getAvailableRooms());
            assertEquals(50000.0, result.get().getPrice());
        }
    }

    @Test
    public void testDeleteKos() {
        Kos kos = kos1;
        doReturn(kos).when(kosRepository).save(kos);
        doReturn(null).when(kosRepository).findById(kos.getId());

        kosService.addKos(kos);
        kosService.deleteKos(kos.getId());

        Optional<Kos> result = kosService.getKosById(kos.getId());

        assertNull(result, "The deleted Kos should be null");
        verify(kosRepository, times(1)).deleteById(kos.getId()); ;
    }

}

