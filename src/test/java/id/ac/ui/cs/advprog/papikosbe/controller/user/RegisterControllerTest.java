// src/test/java/id/ac/ui/cs/advprog/papikosbe/controller/user/RegisterControllerTest.java
package id.ac.ui.cs.advprog.papikosbe.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.papikosbe.exception.DuplicateEmailException;
import id.ac.ui.cs.advprog.papikosbe.model.user.Owner;
import id.ac.ui.cs.advprog.papikosbe.model.user.Tenant;
import id.ac.ui.cs.advprog.papikosbe.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegisterController.class)
@AutoConfigureMockMvc(addFilters = false)        // non‑aktifkan filter security
class RegisterControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    @MockBean UserService userService;

    @Test
    void registerTenantSuccessReturns201() throws Exception {
        Tenant t = Tenant.builder()
                .email("t@mail.com")
                .password("enc")    // hash
                .build();
        t.setId(1L);

        Mockito.when(userService.registerTenant("t@mail.com", "pw"))
                .thenReturn(t);

        mvc.perform(post("/auth/register/tenant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                Map.of("email","t@mail.com","password","pw"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("t@mail.com"));
    }

    @Test
    void registerOwnerSuccessReturns201ApprovedFalse() throws Exception {
        Owner o = Owner.builder()
                .email("o@mail.com")
                .password("enc")
                .build();   // approved default = false
        o.setId(2L);

        Mockito.when(userService.registerOwner("o@mail.com", "pw"))
                .thenReturn(o);

        mvc.perform(post("/auth/register/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                Map.of("email","o@mail.com","password","pw"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.approved").value(false));
    }

    @Test
    void duplicateEmailReturns409() throws Exception {
        Mockito.when(userService.registerTenant(any(), any()))
                .thenThrow(new DuplicateEmailException("dup@mail.com"));

        mvc.perform(post("/auth/register/tenant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                Map.of("email","dup@mail.com","password","pw"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").exists());
    }
}
