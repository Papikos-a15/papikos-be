// src/test/java/id/ac/ui/cs/advprog/papikosbe/controller/AuthControllerTest.java
package id.ac.ui.cs.advprog.papikosbe.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.papikosbe.exception.BadCredentialsException;
import id.ac.ui.cs.advprog.papikosbe.service.user.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import id.ac.ui.cs.advprog.papikosbe.security.JwtTokenProvider;

import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper mapper;

    @MockBean private AuthServiceImpl authService;
    @MockBean JwtTokenProvider jwtProvider;

    @Test
    void loginSuccessReturnsJwt() throws Exception {
        Mockito.when(authService.login("user@mail.com", "pw"))
                .thenReturn("jwt-token");

        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                Map.of("email", "user@mail.com", "password", "pw"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void loginBadCredentialsReturns401() throws Exception {
        Mockito.when(authService.login(anyString(), anyString()))
                .thenThrow(new BadCredentialsException());

        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                Map.of("email", "x@mail.com", "password", "wrong"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void logoutReturns200() throws Exception {
        mvc.perform(post("/auth/logout")
                        .header("Authorization", "Bearer jwt-token"))
                .andExpect(status().isOk());

        Mockito.verify(authService).logout("jwt-token");
    }
}
