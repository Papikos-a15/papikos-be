package id.ac.ui.cs.advprog.papikosbe.controller.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.papikosbe.enums.TransactionType;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Transaction;
import id.ac.ui.cs.advprog.papikosbe.service.transaction.TransactionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    @MockBean TransactionService transactionService;

    @Test
    void createTransactionReturns201() throws Exception {
        UUID userId = UUID.randomUUID();
        Transaction t = Transaction.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .amount(new BigDecimal("100000"))
                .type(TransactionType.TOP_UP)
                .timestamp(LocalDateTime.now())
                .build();

        Mockito.when(transactionService.createTransaction(
                eq(userId),
                eq(new BigDecimal("100000")),
                eq(TransactionType.TOP_UP))
        ).thenReturn(t);

        mvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                Map.of("userId", userId.toString(),
                                        "amount", "100000",
                                        "type", "TOPUP")
                        )))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(t.getId().toString()))
                .andExpect(jsonPath("$.amount").value(100000))
                .andExpect(jsonPath("$.type").value("TOPUP"));
    }

    @Test
    void getAllTransactionsReturns200() throws Exception {
        Transaction t1 = Transaction.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .amount(new BigDecimal("50000"))
                .type(TransactionType.TOP_UP)
                .timestamp(LocalDateTime.now())
                .build();

        Mockito.when(transactionService.findAll())
                .thenReturn(List.of(t1));

        mvc.perform(get("/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getTransactionsByUserIdReturnsList() throws Exception {
        UUID userId = UUID.randomUUID();
        Transaction t1 = Transaction.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .amount(new BigDecimal("50000"))
                .type(TransactionType.TOP_UP)
                .timestamp(LocalDateTime.now())
                .build();

        Mockito.when(transactionService.findAllByUserId(eq(userId)))
                .thenReturn(List.of(t1));

        mvc.perform(get("/transactions/user/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].userId").value(userId.toString()));
    }
}
