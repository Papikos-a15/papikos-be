package id.ac.ui.cs.advprog.papikosbe.controller.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.papikosbe.dto.PaymentRequest;
import id.ac.ui.cs.advprog.papikosbe.dto.TopUpRequest;
import id.ac.ui.cs.advprog.papikosbe.enums.TransactionStatus;
import id.ac.ui.cs.advprog.papikosbe.enums.TransactionType;
import id.ac.ui.cs.advprog.papikosbe.model.user.Tenant;
import id.ac.ui.cs.advprog.papikosbe.model.user.User;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Payment;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.TopUp;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Transaction;
import id.ac.ui.cs.advprog.papikosbe.service.transaction.TransactionService;
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

class TransactionControllerTest {

    private MockMvc mockMvc;
    private TransactionService transactionService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private User tenant;
    private User owner;

    @BeforeEach
    void setUp() {
        transactionService = Mockito.mock(TransactionService.class);
        TransactionController controller = new TransactionController();
        controller.transactionService = transactionService;

        tenant = Tenant.builder()
                .email("tenant@example.com")
                .password("pass")
                .build();
        tenant.setId(UUID.randomUUID());

        owner = Tenant.builder()
                .email("owner@example.com")
                .password("pass")
                .build();
        owner.setId(UUID.randomUUID());

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testGetTransactionById() throws Exception {
        Payment payment = new Payment();
        payment.setId(UUID.randomUUID());
        payment.setAmount(BigDecimal.valueOf(10000));
        payment.setStatus(TransactionStatus.COMPLETED);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUser(tenant);
        payment.setOwner(owner);

        when(transactionService.getTransactionById(payment.getId())).thenReturn(payment);

        mockMvc.perform(get("/api/transactions/" + payment.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(payment.getId().toString()))
                .andExpect(jsonPath("$.type").value("PAYMENT"));
    }

    @Test
    void testCreatePayment() throws Exception {
        PaymentRequest request = new PaymentRequest();
        request.setTenantId(tenant.getId());
        request.setOwnerId(owner.getId());
        request.setAmount(BigDecimal.valueOf(50000));

        Payment payment = new Payment();
        payment.setId(UUID.randomUUID());
        payment.setAmount(request.getAmount());
        payment.setStatus(TransactionStatus.COMPLETED);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUser(tenant);
        payment.setOwner(owner);

        when(transactionService.createPayment(tenant.getId(), owner.getId(), request.getAmount()))
                .thenReturn(payment);

        mockMvc.perform(post("/api/transactions/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(50000))
                .andExpect(jsonPath("$.type").value("PAYMENT"));
    }

    @Test
    void testCreateTopUp() throws Exception {
        TopUpRequest request = new TopUpRequest();
        request.setUserId(tenant.getId());
        request.setAmount(BigDecimal.valueOf(20000));

        TopUp topUp = new TopUp();
        topUp.setId(UUID.randomUUID());
        topUp.setAmount(request.getAmount());
        topUp.setStatus(TransactionStatus.COMPLETED);
        topUp.setCreatedAt(LocalDateTime.now());
        topUp.setUser(tenant);

        when(transactionService.createTopUp(tenant.getId(), request.getAmount())).thenReturn(topUp);

        mockMvc.perform(post("/api/transactions/topup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(20000))
                .andExpect(jsonPath("$.type").value("TOP_UP"));
    }

    @Test
    void testGetPaymentsByTenant() throws Exception {
        Payment payment = new Payment();
        payment.setId(UUID.randomUUID());
        payment.setAmount(BigDecimal.valueOf(30000));
        payment.setStatus(TransactionStatus.COMPLETED);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUser(tenant);
        payment.setOwner(owner);

        when(transactionService.getPaymentsByTenant(tenant.getId()))
                .thenReturn(List.of(payment));

        mockMvc.perform(get("/api/transactions/payment/tenant/" + tenant.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testGetTopUpsByUser() throws Exception {
        TopUp topUp = new TopUp();
        topUp.setId(UUID.randomUUID());
        topUp.setAmount(BigDecimal.valueOf(15000));
        topUp.setStatus(TransactionStatus.COMPLETED);
        topUp.setCreatedAt(LocalDateTime.now());
        topUp.setUser(tenant);

        when(transactionService.getTopUpsByUser(tenant.getId())).thenReturn(List.of(topUp));

        mockMvc.perform(get("/api/transactions/topup/user/" + tenant.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
