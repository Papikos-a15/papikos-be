package id.ac.ui.cs.advprog.papikosbe.controller.user;

import id.ac.ui.cs.advprog.papikosbe.model.user.Owner;
import id.ac.ui.cs.advprog.papikosbe.service.user.OwnerService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OwnerController.class)
@AutoConfigureMockMvc(addFilters = false)
class OwnerControllerTest {

    @Autowired private MockMvc mvc;

    @MockBean private OwnerService ownerService;

    @Test
    void approveOwnerSuccessReturns200() throws Exception {
        UUID id = UUID.randomUUID();

        Owner o = Owner.builder()
                .email("o@mail.com")
                .password("pw")
                .build();
        o.setId(id);
        o.setApproved(true);

        Mockito.when(ownerService.approve(eq(id))).thenReturn(o);

        mvc.perform(patch("/owners/" + id + "/approve")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.approved").value(true));
    }

    @Test
    void approveOwnerNotFoundReturns404() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.when(ownerService.approve(eq(id)))
                .thenThrow(new EntityNotFoundException());

        mvc.perform(patch("/owners/" + id + "/approve")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}