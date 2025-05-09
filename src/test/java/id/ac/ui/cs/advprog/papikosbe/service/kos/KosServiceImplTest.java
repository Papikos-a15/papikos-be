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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
        Kos kos = kos1;
        doReturn(kos).when(kosRepository).save(kos);

        Kos addedKos = kosService.addKos(kos1);

        assertNotNull(addedKos, "The added Kos should not be null");
        assertEquals(kos.getId(), addedKos.getId(), "The Kos ID should be 1L");
        assertEquals(kos.getName(), addedKos.getName());
        verify(kosRepository, times(1)).save(kos1);
    }

    @Test
    public void testGetAllKos() {
        doReturn(Arrays.asList(kos1, kos2)).when(kosRepository).getAllKos();

        List<Kos> result = kosService.getAllKos();
        assertNotNull(result, "The returned list should not be null");
        assertEquals(2, result.size(), "There should be two Kos entries");
        verify(kosRepository, times(1)).getAllKos();
    }

    @Test
    public void testGetKosById() {
        Kos kos = kos1;
        doReturn(kos).when(kosRepository).getKosById(kos1.getId());

        Kos retrievedKos = kosService.getKosById("1234567890");

        assertNotNull(retrievedKos, "Kos should be present for the given ID");
        assertEquals(kos.getName(), retrievedKos.getName());
        verify(kosRepository, times(1)).getKosById("1234567890");
    }

    @Test
    public void testUpdateKos() {
        Kos kos = kos1;
        Kos newKos = new Kos(kos.getId(), "UpdatedKos", "UpdatedAlamatKos", "UpdatedDeskripsiKos",
                75000.0);
        doReturn(newKos).when(kosRepository).updateKos(kos.getId(), newKos);

        Kos result = kosService.updateKos(kos.getId(), newKos);

        assertNotNull(result, "The updated Kos should not be null");
        assertEquals("1234567890", result.getId());
        assertEquals("UpdatedKos", result.getName());
        assertEquals("UpdatedAlamatKos", result.getAddress());
        assertEquals("UpdatedDeskripsiKos", result.getDescription());
        assertEquals(75000.0, result.getPrice());
        verify(kosRepository, times(1)).updateKos(kos.getId(), newKos);
    }

    @Test
    public void testDeleteKos() {
        Kos kos = kos1;
        doReturn(kos).when(kosRepository).save(kos);
        doReturn(null).when(kosRepository).getKosById(kos.getId());

        kosService.addKos(kos);
        kosService.deleteKos(kos.getId());

        Kos result = kosService.getKosById(kos.getId());

        assertNull(result, "The deleted Kos should be null");
        verify(kosRepository, times(1)).deleteKos("1234567890") ;
    }
}

