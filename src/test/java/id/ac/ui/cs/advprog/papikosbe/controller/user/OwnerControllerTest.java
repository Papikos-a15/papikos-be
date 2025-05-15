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

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

        mvc.perform(patch("/api/owners/" + id + "/approve")
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

        mvc.perform(patch("/api/owners/" + id + "/approve")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUnapprovedOwnersReturns200() throws Exception {
        Owner o1 = Owner.builder()
                .email("unapproved1@mail.com")
                .password("pw")
                .build();
        o1.setId(UUID.randomUUID());

        Owner o2 = Owner.builder()
                .email("unapproved2@mail.com")
                .password("pw")
                .build();
        o2.setId(UUID.randomUUID());

        Mockito.when(ownerService.findUnapprovedOwners()).thenReturn(List.of(o1, o2));

        mvc.perform(get("/api/owners/unapproved")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].email").value("unapproved1@mail.com"))
                .andExpect(jsonPath("$[1].email").value("unapproved2@mail.com"));
    }

    @Test
    void getOwnerEmailByIdSuccessReturns200() throws Exception {
        UUID id = UUID.randomUUID();
        String email = "owner@example.com";

        Owner owner = Owner.builder()
                .email(email)
                .password("password")
                .build();
        owner.setId(id);

        Mockito.when(ownerService.findOwnerById(eq(id))).thenReturn(owner);

        mvc.perform(get("/api/owners/" + id + "/email")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(email));
    }

    @Test
    void getOwnerEmailByIdNotFoundReturns404() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.when(ownerService.findOwnerById(eq(id)))
                .thenThrow(new EntityNotFoundException());

        mvc.perform(get("/api/owners/" + id + "/email")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}