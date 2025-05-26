package id.ac.ui.cs.advprog.papikosbe.controller.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.papikosbe.enums.WalletStatus;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Wallet;
import id.ac.ui.cs.advprog.papikosbe.model.user.Tenant;
import id.ac.ui.cs.advprog.papikosbe.model.user.User;
import id.ac.ui.cs.advprog.papikosbe.repository.user.UserRepository;
import id.ac.ui.cs.advprog.papikosbe.service.transaction.WalletService;
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

    @BeforeEach
    void setUp() {
        walletService = Mockito.mock(WalletService.class);
        WalletController walletController = new WalletController(walletService);
        mockMvc = MockMvcBuilders.standaloneSetup(walletController).build();
    }

    @Test
    void testCreateWallet() throws Exception {
        UUID userId = UUID.randomUUID();

        User dummyUser = Tenant.builder()
                .email("dummy@example.com")
                .password("securepassword")
                .build();
        dummyUser.setId(userId);

        Wallet wallet = new Wallet(dummyUser, BigDecimal.ZERO);
        wallet.setId(UUID.randomUUID());

        when(walletService.create(userId)).thenReturn(wallet);

        mockMvc.perform(post("/api/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": \"" + userId.toString() + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.id").doesNotExist());
    }

    @Test
    void testFindAllWallets() throws Exception {
        User user1 = Tenant.builder().email("nae@gmail.com").password("123").build();
        User user2 = Tenant.builder().email("farah@gmail.com").password("456").build();

        Wallet wallet1 = new Wallet(user1, new BigDecimal("100.00"));
        wallet1.setId(UUID.randomUUID());

        Wallet wallet2 = new Wallet(user2, new BigDecimal("200.00"));
        wallet2.setId(UUID.randomUUID());

        when(walletService.findAll()).thenReturn(List.of(wallet1, wallet2));

        mockMvc.perform(get("/api/wallets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testFindWalletById() throws Exception {
        User user1 = Tenant.builder().email("nae@gmail.com").password("123").build();

        Wallet wallet = new Wallet(user1, new BigDecimal("100.00"));
        wallet.setId(UUID.randomUUID());

        when(walletService.findById(wallet.getId())).thenReturn(wallet);

        mockMvc.perform(get("/api/wallets/" + wallet.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(wallet.getId().toString()));
    }

    @Test
    void testFindWalletByUserId() throws Exception {
        UUID userId = UUID.randomUUID();
        User dummyUser = Tenant.builder()
                .email("dummy@example.com")
                .password("securepassword")
                .build();
        dummyUser.setId(userId);

        Wallet wallet = new Wallet(dummyUser, new BigDecimal("100.00"));
        wallet.setId(UUID.randomUUID());
        wallet.setStatus(WalletStatus.ACTIVE);

        when(walletService.findByUserId(userId)).thenReturn(wallet);

        mockMvc.perform(get("/api/wallets/user/" + userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(wallet.getId().toString()))
                .andExpect(jsonPath("$.balance").value(wallet.getBalance().doubleValue()))
                .andExpect(jsonPath("$.status").value(wallet.getStatus().toString()))
                .andExpect(jsonPath("$.userId").value(userId.toString()));
    }

    @Test
    void testEditWallet() throws Exception {
        UUID walletId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        String jsonContent = String.format("""
        {
            "id": "%s",
            "user": {
                "id": "%s"
            },
            "balance": 300.00
        }
        """, walletId, userId);

        User dummyUser = new Tenant();
        dummyUser.setId(userId);

        Wallet updatedWallet = new Wallet(dummyUser, new BigDecimal("300.00"));
        updatedWallet.setId(walletId);

        when(walletService.edit(eq(walletId), any(Wallet.class))).thenReturn(updatedWallet);

        mockMvc.perform(put("/api/wallets/" + walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
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
