package id.ac.ui.cs.advprog.papikosbe.controller.kos;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.papikosbe.config.SecurityConfig;
import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import id.ac.ui.cs.advprog.papikosbe.security.JwtTokenProvider;
import id.ac.ui.cs.advprog.papikosbe.service.kos.KosService;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(KosController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
public class KosControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private KosService kosService;

    @MockitoBean
    private JwtTokenProvider jwtProvider;

    @InjectMocks
    private KosController kosController;

    @Autowired
    private ObjectMapper objectMapper;

    private Kos dummy;

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

        when(jwtProvider.validate("tok")).thenReturn(true);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "user", null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        when(jwtProvider.getAuthentication("tok")).thenReturn(auth);
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
        when(kosService.getAllKos()).thenReturn(List.of(dummy));

        mockMvc.perform(get("/api/management/list")
                        .header("Authorization", "Bearer tok"))
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

        mockMvc.perform(get("/api/management/"+randomId.toString())
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

        mockMvc.perform(post("/api/management/addAvailable")
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

        mockMvc.perform(post("/api/management/subtractAvailable")
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
}
