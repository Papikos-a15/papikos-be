package id.ac.ui.cs.advprog.papikosbe.controller.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.papikosbe.controller.TopUpController;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.TopUp;
import id.ac.ui.cs.advprog.papikosbe.service.transaction.TopUpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TopUpControllerTest {

    private MockMvc mockMvc;
    private TopUpService topUpService;
    private ObjectMapper objectMapper;

    private UUID userId;
    private TopUp topUp;

    @BeforeEach
    void setUp() {
        topUpService = Mockito.mock(TopUpService.class);
        TopUpController topUpController = new TopUpController(topUpService);
        mockMvc = MockMvcBuilders.standaloneSetup(topUpController).build();
        objectMapper = new ObjectMapper();

        userId = UUID.randomUUID();
        topUp = new TopUp(UUID.randomUUID(), userId, new BigDecimal("200.00"), LocalDateTime.now());
    }

    @Test
    void testCreateTopUp() throws Exception {
        when(topUpService.createTopUp(userId, new BigDecimal("200.00"))).thenReturn(topUp);

        mockMvc.perform(post("/api/topups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateTopUpRequest(userId, new BigDecimal("200.00"))
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.amount").value(200.00));
    }

    @Test
    void testFindAllTopUps() throws Exception {
        when(topUpService.findAll()).thenReturn(List.of(topUp));

        mockMvc.perform(get("/api/topups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(userId.toString()));
    }

    @Test
    void testFindTopUpById() throws Exception {
        when(topUpService.findById(topUp.getId())).thenReturn(topUp);

        mockMvc.perform(get("/api/topups/" + topUp.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(topUp.getId().toString()));
    }

    @Test
    void testFindTopUpByUserId() throws Exception {
        when(topUpService.findByUserId(userId)).thenReturn(List.of(topUp));

        mockMvc.perform(get("/api/topups/user/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(userId.toString()));
    }

    // DTO inner class for the request body
    record CreateTopUpRequest(UUID userId, BigDecimal amount) {}
}
