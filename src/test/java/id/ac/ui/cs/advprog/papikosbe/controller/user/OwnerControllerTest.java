// src/test/java/id/ac/ui/cs/advprog/papikosbe/controller/user/OwnerControllerTest.java
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

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OwnerController.class)
@AutoConfigureMockMvc(addFilters = false)          // ⬅ nonaktifkan filter Security
class OwnerControllerTest {

    @Autowired private MockMvc mvc;

    @MockBean private OwnerService ownerService;   // ⬅ mock interface‑nya

    @Test
    void approveOwnerSuccessReturns200() throws Exception {
        Owner o = Owner.builder()
                .email("o@mail.com")
                .password("pw")
                .build();
        o.setId(42L);
        o.setApproved(true);

        Mockito.when(ownerService.approve(42L)).thenReturn(o);

        mvc.perform(patch("/owners/42/approve")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.approved").value(true));
    }

    @Test
    void approveOwnerNotFoundReturns404() throws Exception {
        Mockito.when(ownerService.approve(eq(99L)))
                .thenThrow(new EntityNotFoundException());

        mvc.perform(patch("/owners/99/approve")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
