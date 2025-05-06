package id.ac.ui.cs.advprog.papikosbe.controller.test;

import id.ac.ui.cs.advprog.papikosbe.config.SecurityConfig;
import id.ac.ui.cs.advprog.papikosbe.security.JwtTokenProvider;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProtectedController.class)
// Import SecurityConfig agar bean‐bean security (filterChain, JwtFilter, entry‐point) ikut dimuat
@Import(SecurityConfig.class)
// Pastikan MockMvc juga meng-attach seluruh filter chain, termasuk Security
@AutoConfigureMockMvc
class ProtectedControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private JwtTokenProvider jwtProvider;

    @Test
    void tanpaToken_menolakDenganUnauthorized() throws Exception {
        mvc.perform(get("/protected"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void denganTokenTidakValid_menolakDenganUnauthorized() throws Exception {
        when(jwtProvider.validate("invalid")).thenReturn(false);

        mvc.perform(get("/protected")
                        .header("Authorization", "Bearer invalid"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void denganTokenValid_mengembalikanOk() throws Exception {
        when(jwtProvider.validate("tok")).thenReturn(true);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                "user", null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        when(jwtProvider.getAuthentication("tok")).thenReturn(auth);

        mvc.perform(get("/protected")
                        .header("Authorization", "Bearer tok"))
                .andExpect(status().isOk())
                .andExpect(content().string("ok"));
    }
}
