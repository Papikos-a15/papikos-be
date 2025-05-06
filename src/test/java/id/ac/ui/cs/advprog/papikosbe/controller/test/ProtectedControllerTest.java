// src/test/java/id/ac/ui/cs/advprog/papikosbe/controller/ProtectedControllerTest.java
package id.ac.ui.cs.advprog.papikosbe.controller.test;

import id.ac.ui.cs.advprog.papikosbe.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Jangan matikan filter; biarkan SecurityFilterChain aktif
@WebMvcTest(ProtectedController.class)
class ProtectedControllerTest {

    @Autowired
    private MockMvc mvc;

    // kita hanya perlu mock JwtTokenProvider,
    // filter akan memanggil validate() dan getAuthentication()
    @MockBean
    private JwtTokenProvider jwtProvider;

    @Test
    void tanpaToken_menolakDenganUnauthorized() throws Exception {
        mvc.perform(get("/protected"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void denganTokenTidakValid_menolakDenganUnauthorized() throws Exception {
        // header ada tapi token invalid
        when(jwtProvider.validate("invalid")).thenReturn(false);

        mvc.perform(get("/protected")
                        .header("Authorization", "Bearer invalid"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void denganTokenValid_mengembalikanOk() throws Exception {
        // stub: token valid
        when(jwtProvider.validate("tok")).thenReturn(true);
        // stub: dapatkan Authentication dari provider
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
