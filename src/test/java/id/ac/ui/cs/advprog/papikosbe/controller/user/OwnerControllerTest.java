// src/test/java/id/ac/ui/cs/advprog/papikosbe/controller/OwnerControllerTest.java
package id.ac.ui.cs.advprog.papikosbe.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.papikosbe.model.user.Owner;
import id.ac.ui.cs.advprog.papikosbe.service.user.OwnerServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.persistence.EntityNotFoundException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.eq;

@WebMvcTest(OwnerController.class)
class OwnerControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper mapper;

    @MockBean private OwnerServiceImpl ownerService;

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
