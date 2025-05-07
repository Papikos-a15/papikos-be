package id.ac.ui.cs.advprog.papikosbe.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.papikosbe.model.Wallet;
import id.ac.ui.cs.advprog.papikosbe.service.WalletService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class WalletControllerTest {

    private MockMvc mockMvc;
    private WalletService walletService;
    private WalletController walletController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        walletService = Mockito.mock(WalletService.class);
        walletController = new WalletController(walletService);
        mockMvc = MockMvcBuilders.standaloneSetup(walletController).build();
    }

    @Test
    void testCreateWallet() throws Exception {
        UUID userId = UUID.randomUUID();
        Wallet wallet = new Wallet(UUID.randomUUID(), userId, BigDecimal.ZERO);

        when(walletService.create(userId)).thenReturn(wallet);

        mockMvc.perform(post("/api/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": \"" + userId.toString() + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()));
    }


    @Test
    void testFindAllWallets() throws Exception {
        Wallet wallet1 = new Wallet(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("100.00"));
        Wallet wallet2 = new Wallet(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("200.00"));

        when(walletService.findAll()).thenReturn(List.of(wallet1, wallet2));

        mockMvc.perform(get("/api/wallets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testFindWalletById() throws Exception {
        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet(walletId, UUID.randomUUID(), new BigDecimal("150.00"));

        when(walletService.findById(walletId)).thenReturn(wallet);

        mockMvc.perform(get("/api/wallets/" + walletId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(walletId.toString()));
    }

    @Test
    void testEditWallet() throws Exception {
        UUID walletId = UUID.randomUUID();
        Wallet updatedWallet = new Wallet(walletId, UUID.randomUUID(), new BigDecimal("300.00"));

        when(walletService.edit(eq(walletId), any(Wallet.class))).thenReturn(updatedWallet);

        mockMvc.perform(put("/api/wallets/" + walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedWallet)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance", Matchers.is(300.0)));
    }

    @Test
    void testDeleteWallet() throws Exception {
        UUID walletId = UUID.randomUUID();

        doNothing().when(walletService).delete(walletId);

        mockMvc.perform(delete("/api/wallets/" + walletId))
                .andExpect(status().isOk());
    }
}
