package id.ac.ui.cs.advprog.papikosbe.controller.kos;

import id.ac.ui.cs.advprog.papikosbe.service.kos.KosService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class KosControllerTest {
    @Mock
    private KosService kosService;

    @InjectMocks
    private KosController kosController;

    @BeforeEach
    void setUp() {}

    @Test
    void addKos_returnsCreated() throws Exception {}

    @Test
    void getAllKos_returnsList()  throws Exception {}

    @Test
    void getKosById_found() throws Exception {}

    @Test
    void getKosById_notFound() throws Exception {}

    @Test
    void updateKos_returnsUpdated() throws Exception {}

    @Test
    void deleteKos_returnsDeleted() throws Exception {}
}
