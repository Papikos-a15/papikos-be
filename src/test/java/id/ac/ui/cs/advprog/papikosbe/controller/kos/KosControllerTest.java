package id.ac.ui.cs.advprog.papikosbe.controller.kos;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.papikosbe.config.SecurityConfig;
import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import id.ac.ui.cs.advprog.papikosbe.security.JwtTokenProvider;
import id.ac.ui.cs.advprog.papikosbe.service.kos.KosSearchService;
import id.ac.ui.cs.advprog.papikosbe.service.kos.KosService;
import id.ac.ui.cs.advprog.papikosbe.util.AuthenticationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import java.util.*;
import java.util.concurrent.CompletableFuture;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(KosController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
@WithMockUser
public class KosControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private KosService kosService;

    @MockitoBean
    private JwtTokenProvider jwtProvider;

    @MockitoBean
    private AuthenticationUtils authUtils;

    @InjectMocks
    private KosController kosController;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private KosSearchService kosSearchService;

    private List<Kos> testKosList;
    private Kos testKos1, testKos2;

    private Kos dummy;
    private UUID userId;

    @BeforeEach
    void setUp() {
        dummy = new Kos(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Kos1",
                "Addr Kos1",
                "Description",
                1500000.0,
                30
        );
        dummy.setAvailableRooms(30);
        dummy.setAvailable(true);

        // Set up test data
        testKos1 = new Kos();
        testKos1.setId(UUID.randomUUID());
        testKos1.setName("Kos A");
        testKos1.setAddress("Jalan Margonda 10");
        testKos1.setPrice(1000000.0);
        testKos1.setAvailable(true);

        testKos2 = new Kos();
        testKos2.setId(UUID.randomUUID());
        testKos2.setName("Kos B");
        testKos2.setAddress("Jalan Pondok Cina 20");
        testKos2.setPrice(1500000.0);
        testKos2.setAvailable(false);

        testKosList = Arrays.asList(testKos1, testKos2);


        when(jwtProvider.validate("tok")).thenReturn(true);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "user", null, List.of(new SimpleGrantedAuthority("ROLE_OWNER"))
        );
        when(jwtProvider.getAuthentication("tok")).thenReturn(auth);

        // Authentication utils stubs
        when(authUtils.getUserIdFromAuth(any())).thenReturn(userId);
    }

    @Test
    void addKos_returnsCreated() throws Exception {
        when(kosService.addKos(any())).thenReturn(dummy);

        mockMvc.perform(post("/api/management/add")
                        .header("Authorization", "Bearer tok")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dummy)))
                .andExpect(status().isCreated())
                .andExpect((jsonPath("$.id").value(dummy.getId().toString())))
                .andExpect((jsonPath("$.available").value(dummy.isAvailable())));
    }

    @Test
    void getAllKos_returnsList() throws Exception {
        when(kosService.getAllKos()).thenReturn(CompletableFuture.completedFuture(List.of(dummy)));

        MvcResult mvcResult = mockMvc.perform(get("/api/management/list")
                        .header("Authorization", "Bearer tok"))
                        .andExpect(request().asyncStarted())  // Check async started
                        .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))  // Dispatch async result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(dummy.getId().toString()));
    }

    @Test
    void getKosById_found() throws Exception {
        when(kosService.getKosById(any())).thenReturn(Optional.ofNullable(dummy));

        mockMvc.perform(get("/api/management/"+dummy.getId().toString())
                        .header("Authorization", "Bearer tok"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dummy.getId().toString()));
    }

    @Test
    void getKosById_notFound() throws Exception {
        UUID randomId = UUID.randomUUID();
        when(kosService.getKosById(randomId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/management/"+randomId)
                        .header("Authorization", "Bearer tok"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateKos_returnsUpdated() throws Exception {
        Kos newKos = new Kos(
            dummy.getId(),
            UUID.randomUUID(),
            "Kos2",
            "Addr Kos2",
            "Description Kos2",
            1200000.0,
            30
        );
        when(kosService.updateKos(any(), any())).thenReturn(Optional.ofNullable(dummy));

        mockMvc.perform(patch("/api/management/update/"+dummy.getId().toString())
                        .header("Authorization", "Bearer tok")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newKos)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(newKos.getId().toString()))
                .andExpect(jsonPath("$.available").value(dummy.isAvailable()));
    }

    @Test
    void addAvailable_returnsAdded() throws Exception {
        when(kosService.getKosById(any())).thenReturn(Optional.ofNullable(dummy));
        when(kosService.addAvailableRoom(dummy.getId())).thenReturn(Optional.of(dummy));
        dummy.setAvailableRooms(29);


        mockMvc.perform(patch("/api/management/addAvailable")
                    .header("Authorization", "Bearer tok")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dummy)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dummy.getId().toString()))
                .andExpect(jsonPath("$.availableRooms").value(dummy.getAvailableRooms()));
    }

    @Test
    void subtractAvailable_returnsRemoved() throws Exception {
        when(kosService.getKosById(any())).thenReturn(Optional.ofNullable(dummy));
        when(kosService.subtractAvailableRoom(dummy.getId())).thenReturn(Optional.of(dummy));

        mockMvc.perform(patch("/api/management/subtractAvailable")
                        .header("Authorization", "Bearer tok")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dummy)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dummy.getId().toString()))
                .andExpect(jsonPath("$.availableRooms").value(dummy.getAvailableRooms()));
    }

    @Test
    void deleteKos_returnsDeleted() throws Exception {
        mockMvc.perform(delete("/api/management/delete/"+dummy.getId().toString())
                        .header("Authorization", "Bearer tok"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void searchKosBySingleCriteria_ShouldReturnFilteredResults() throws Exception {
        // Mock service response with CompletableFuture
        when(kosService.getAllKos()).thenReturn(CompletableFuture.completedFuture(testKosList));
        when(kosSearchService.search(eq(testKosList), eq("name"), eq("Kos A")))
                .thenReturn(Collections.singletonList(testKos1));

        // Perform request - expect async started
        MvcResult mvcResult = mockMvc.perform(get("/api/management/search")
                        .param("name", "Kos A")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer tok"))
                .andExpect(request().asyncStarted())
                .andReturn();

        // Handle async result
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testKos1.getId().toString()))
                .andExpect(jsonPath("$[0].name").value("Kos A"));
    }

    @Test
    @WithMockUser
    void searchKosByPriceRange_ShouldReturnFilteredResults() throws Exception {
        // Mock service response for price range search with CompletableFuture
        when(kosService.getAllKos()).thenReturn(CompletableFuture.completedFuture(testKosList));

        // Mock search with price range criteria
        when(kosSearchService.multiSearch(eq(testKosList), any(Map.class)))
                .thenReturn(Collections.singletonList(testKos1));

        MvcResult mvcResult = mockMvc.perform(get("/api/management/search")
                        .param("minPrice", "800000")
                        .param("maxPrice", "1200000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer tok"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Kos A"));
    }

    @Test
    @WithMockUser
    void searchKosWithMultipleCriteria_ShouldReturnFilteredResults() throws Exception {
        when(kosService.getAllKos()).thenReturn(CompletableFuture.completedFuture(testKosList));
        when(kosSearchService.multiSearch(eq(testKosList), any(Map.class)))
                .thenReturn(Collections.singletonList(testKos1));

        MvcResult mvcResult = mockMvc.perform(get("/api/management/search")
                        .param("name", "Kos")
                        .param("location", "Margonda")
                        .param("availability", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer tok"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testKos1.getId().toString()));
    }

    @Test
    @WithMockUser
    void searchKosWithNoParameters_ShouldReturnAllKos() throws Exception {
        when(kosService.getAllKos()).thenReturn(CompletableFuture.completedFuture(testKosList));

        MvcResult mvcResult = mockMvc.perform(get("/api/management/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer tok"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testKos1.getId().toString()))
                .andExpect(jsonPath("$[1].id").value(testKos2.getId().toString()));
    }
    
}
