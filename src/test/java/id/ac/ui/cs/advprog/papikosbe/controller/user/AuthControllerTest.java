// src/test/java/id/ac/ui/cs/advprog/papikosbe/controller/user/AuthControllerTest.java
package id.ac.ui.cs.advprog.papikosbe.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.papikosbe.controller.user.dto.TokenResponse;
import id.ac.ui.cs.advprog.papikosbe.exception.BadCredentialsException;
import id.ac.ui.cs.advprog.papikosbe.service.user.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)   // ⬅ nonaktifkan filter Security
class AuthControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    @MockBean AuthService authService;

    @Test
    void loginSuccessReturnsJwt() throws Exception {
        Mockito.when(authService.login("user@mail.com","pw"))
                .thenReturn("jwt-token");

        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                Map.of("email","user@mail.com","password","pw"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void loginBadCredentialsReturns401() throws Exception {
        Mockito.when(authService.login(anyString(),anyString()))
                .thenThrow(new BadCredentialsException());

        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                Map.of("email","x@mail.com","password","wrong"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void logoutReturns200() throws Exception {
        mvc.perform(post("/api/auth/logout")
                        .header("Authorization","Bearer jwt-token"))
                .andExpect(status().isOk());

        Mockito.verify(authService).logout("jwt-token");
    }

    @Test
    void testConstructorAndGetters() {
        UUID userId = UUID.randomUUID();
        String token = "abc.def.ghi";

        TokenResponse response = new TokenResponse(token, userId);

        assertEquals(token, response.getToken());
        assertEquals(userId, response.getUserId());
    }

    @Test
    void testSetters() {
        TokenResponse response = new TokenResponse(null, null);

        UUID newUserId = UUID.randomUUID();
        String newToken = "xyz.123";

        response.setToken(newToken);
        response.setUserId(newUserId);

        assertEquals(newToken, response.getToken());
        assertEquals(newUserId, response.getUserId());
    }
}
