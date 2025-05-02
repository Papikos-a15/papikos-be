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
        kos1.setId("1234567890");
        kos1.setName("Kos1");
        kos1.setAddress("AlamatKos1");
        kos1.setDescription("DeskripsiKos1");
        kos1.setPrice(50000.0);

        kos2 = new Kos();
        kos2.setId("1234567891");
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
        assertEquals("1234567890", addedKos.getId(), "The Kos ID should be 1L");
        assertEquals("Kos1", addedKos.getName());
        verify(kosRepository, times(1)).save(kos1);
    }

    @Test
    public void testGetAllKos() {
        when(kosRepository.getAllKos()).thenReturn(Arrays.asList(kos1, kos2));

        List<Kos> result = kosService.getAllKos();

        assertNotNull(result, "The returned list should not be null");
        assertEquals(2, result.size(), "There should be two Kos entries");
        verify(kosRepository, times(1)).getAllKos();
    }

    @Test
    public void testGetKosById() {
        when(kosRepository.getKosById("1234567890")).thenReturn(kos1);

        Kos retrievedKos = kosService.getKosById("1234567890");

        assertNotNull(retrievedKos, "Kos should be present for the given ID");
        assertEquals("Kos1", retrievedKos.getName());
        verify(kosRepository, times(1)).getKosById("1234567890");
    }

//    @Test
//    public void testUpdateKos() {
//        Kos addedKos = kosService.addKos(kos1);
//        System.out.println(addedKos);
//
//        Kos updatedVersion = new Kos();
//        updatedVersion.setName("UpdatedKos");
//        updatedVersion.setAddress("UpdatedAlamatKos");
//        updatedVersion.setDescription("UpdatedDeskripsiKos");
//        updatedVersion.setPrice(75000.0);
//
//        Kos result = kosService.updateKos("1234567890", updatedVersion);
//
//        assertNotNull(addedKos, "The updated Kos should not be null");
//        assertEquals("1234567890", result.getId());
//        assertEquals("UpdatedKos1", result.getName());
//        assertEquals("UpdatedAlamatKos1", result.getAddress());
//        assertEquals("UpdatedDeskripsiKos1", result.getDescription());
//        assertEquals(75000.0, result.getPrice());
//        verify(kosRepository, times(1)).getKosById("1234567890");
//        verify(kosRepository, times(1)).save(any(Kos.class));
//    }

    @Test
    public void testDeleteKos() {
        kosService.addKos(kos1);
        kosService.deleteKos("1234567890");

        verify(kosRepository, times(1)).deleteKos("1234567890") ;
    }
}

