package id.ac.ui.cs.advprog.papikosbe.service;

import id.ac.ui.cs.advprog.papikosbe.model.Kos;
import id.ac.ui.cs.advprog.papikosbe.repository.KosRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class KosServiceTest {

    @Mock
    private KosRepository kosRepository;

    @InjectMocks
    private KosServiceImpl kosService;

    private Kos kos1;
    private Kos kos2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        kos1 = new Kos();
        kos1.setId(1L);
        kos1.setName("Kos1");
        kos1.setAddress("AlamatKos1");
        kos1.setDescription("DeskripsiKos1");
        kos1.setPrice(50000.0);

        kos2 = new Kos();
        kos2.setId(2L);
        kos2.setName("Kos2");
        kos2.setAddress("AlamatKos2");
        kos2.setDescription("DeskripsiKos2");
        kos2.setPrice(60000.0);
    }

    @Test
    public void testAddKos() {
        when(kosRepository.save(any(Kos.class))).thenReturn(kos1);

        Kos addedKos = kosService.addKos(kos1);

        assertNotNull(addedKos, "The added Kos should not be null");
        assertEquals(1L, addedKos.getId(), "The Kos ID should be 1L");
        assertEquals("Kos1", addedKos.getName());
        verify(kosRepository, times(1)).save(kos1);
    }

    @Test
    public void testGetAllKos() {
        when(kosRepository.findAll()).thenReturn(Arrays.asList(kos1, kos2));

        List<Kos> result = kosService.getAllKos();

        assertNotNull(result, "The returned list should not be null");
        assertEquals(2, result.size(), "There should be two Kos entries");
        verify(kosRepository, times(1)).findAll();
    }

    @Test
    public void testGetKosById() {
        when(kosRepository.findById(1L)).thenReturn(Optional.of(kos1));

        Optional<Kos> retrievedKos = kosService.getKosById(1L);

        assertTrue(retrievedKos.isPresent(), "Kos should be present for the given ID");
        assertEquals("Kos1", retrievedKos.get().getName());
        verify(kosRepository, times(1)).findById(1L);
    }

    @Test
    public void testUpdateKos() {
        Kos updatedKos = new Kos();
        updatedKos.setName("UpdatedKos1");
        updatedKos.setAddress("UpdatedAlamatKos1");
        updatedKos.setDescription("UpdatedDeskripsiKos1");
        updatedKos.setPrice(75000.0);

        when(kosRepository.findById(1L)).thenReturn(Optional.of(kos1));

        Kos updatedVersion = new Kos();
        updatedVersion.setId(1L);
        updatedVersion.setName("UpdatedKos1");
        updatedVersion.setAddress("UpdatedAlamatKos1");
        updatedVersion.setDescription("UpdatedDeskripsiKos1");
        updatedVersion.setPrice(75000.0);
        when(kosRepository.save(any(Kos.class))).thenReturn(updatedVersion);

        Kos result = kosService.updateKos(1L, updatedKos);

        assertNotNull(result, "The updated Kos should not be null");
        assertEquals(1L, result.getId());
        assertEquals("UpdatedKos1", result.getName());
        assertEquals("UpdatedAlamatKos1", result.getAddress());
        assertEquals("UpdatedDeskripsiKos1", result.getDescription());
        assertEquals(75000.0, result.getPrice());
        verify(kosRepository, times(1)).findById(1L);
        verify(kosRepository, times(1)).save(any(Kos.class));
    }

    @Test
    public void testDeleteKos() {
        kosService.deleteKos(1L);

        verify(kosRepository, times(1)).deleteById(1L);
    }
}

