// src/test/java/id/ac/ui/cs/advprog/papikosbe/security/JwtAuthenticationFilterTest.java
package id.ac.ui.cs.advprog.papikosbe.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock private JwtTokenProvider jwtProvider;
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(jwtProvider);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilter_validBearer_setsAuthentication() throws ServletException, IOException {
        // given
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader("Authorization", "Bearer good.token");
        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        when(jwtProvider.validate("good.token")).thenReturn(true);
        Authentication auth =
                new UsernamePasswordAuthenticationToken("user", null, List.of());
        // asumsi JwtTokenProvider sekarang punya getAuthentication(...)
        when(jwtProvider.getAuthentication("good.token")).thenReturn(auth);

        // when
        filter.doFilterInternal(req, res, chain);

        // then
        Authentication actual = SecurityContextHolder.getContext().getAuthentication();
        assertThat(actual).isSameAs(auth);
        verify(chain).doFilter(req, res);
    }

    @Test
    void doFilter_missingHeader_doesNotSetAuthentication() throws ServletException, IOException {
        // given
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        // when
        filter.doFilterInternal(req, res, chain);

        // then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(chain).doFilter(req, res);
        verifyNoInteractions(jwtProvider);
    }

    @Test
    void doFilter_invalidToken_doesNotSetAuthentication() throws ServletException, IOException {
        // given
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader("Authorization", "Bearer bad.token");
        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        when(jwtProvider.validate("bad.token")).thenReturn(false);

        // when
        filter.doFilterInternal(req, res, chain);

        // then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(chain).doFilter(req, res);
        verify(jwtProvider).validate("bad.token");
        verify(jwtProvider, never()).getAuthentication(any());
    }
}
