package id.ac.ui.cs.advprog.papikosbe.controller.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Payment;
import id.ac.ui.cs.advprog.papikosbe.service.transaction.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PaymentControllerTest {
    private MockMvc mockMvc;
    private PaymentServiceImpl paymentService;

    @BeforeEach
    void setUp() {
        paymentService = mock(PaymentServiceImpl.class);
        PaymentController paymentController = new PaymentController(paymentService);
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();
    }

    @Test
    void testCreatePayment() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100.00");

        Payment payment = new Payment(UUID.randomUUID(), userId, ownerId, amount, LocalDateTime.now());

        when(paymentService.createPayment(userId, ownerId, amount)).thenReturn(payment);

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(Map.of(
                                "userId", userId.toString(),
                                "ownerId", ownerId.toString(),
                                "amount", amount.toString()
                        ))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()));
    }


    @Test
    void testFindAllPayments() throws Exception {
        Payment payment = new Payment(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("50.00"), LocalDateTime.now());
        when(paymentService.findAll()).thenReturn(List.of(payment));

        mockMvc.perform(get("/api/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testFindPaymentById() throws Exception {
        UUID id = UUID.randomUUID();
        Payment payment = new Payment(id, UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("75.00"), LocalDateTime.now());
        when(paymentService.findById(id)).thenReturn(payment);

        mockMvc.perform(get("/api/payments/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void testFindPaymentsByUserId() throws Exception {
        UUID userId = UUID.randomUUID();
        Payment payment = new Payment(UUID.randomUUID(), userId, UUID.randomUUID(), new BigDecimal("60.00"), LocalDateTime.now());
        when(paymentService.findByUserId(userId)).thenReturn(List.of(payment));

        mockMvc.perform(get("/api/payments/user/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].userId").value(userId.toString()));
    }

    @Test
    void testFindPaymentsByDate() throws Exception {
        LocalDateTime now = LocalDateTime.of(2025, 4, 30, 0, 0);
        Payment payment = new Payment(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("90.00"), now);
        when(paymentService.findByDate(now)).thenReturn(List.of(payment));

        mockMvc.perform(get("/api/payments/date")
                        .param("date", now.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
