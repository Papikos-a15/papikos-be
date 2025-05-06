// src/test/java/id/ac/ui/cs/advprog/papikosbe/service/AuthServiceTest.java
package id.ac.ui.cs.advprog.papikosbe.service.user;

import id.ac.ui.cs.advprog.papikosbe.exception.BadCredentialsException;
import id.ac.ui.cs.advprog.papikosbe.model.user.Owner;
import id.ac.ui.cs.advprog.papikosbe.model.user.Tenant;
import id.ac.ui.cs.advprog.papikosbe.model.user.User;
import id.ac.ui.cs.advprog.papikosbe.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import id.ac.ui.cs.advprog.papikosbe.security.JwtTokenProvider;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository userRepo;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtTokenProvider jwtProvider;
    @InjectMocks AuthService authService;

    private Tenant tenant;
    private Owner ownerUnapproved, ownerApproved;

    @BeforeEach
    void setUp() {
        tenant = Tenant.builder()
                .email("t@mail.com")
                .password("encodedTenantPwd")
                .build();

        ownerUnapproved = Owner.builder()
                .email("ou@mail.com")
                .password("encodedOwnerPwd")
                .build();

        ownerApproved = Owner.builder()
                .email("oa@mail.com")
                .password("encOwnerPwd")
                .build();
        ownerApproved.setApproved(true);
    }

    @Test
    void testLoginTenantSuccess() {
        when(userRepo.findByEmail("t@mail.com"))
                .thenReturn(Optional.of(tenant));
        when(passwordEncoder.matches("rawTenantPwd", tenant.getPassword()))
                .thenReturn(true);
        when(jwtProvider.createToken(argThat(u->u.equals(tenant))))
                .thenReturn("jwt-token");

        String token = authService.login("t@mail.com","rawTenantPwd");

        assertThat(token).isEqualTo("jwt-token");
        verify(jwtProvider).createToken(tenant);
    }

    @Test
    void testLoginOwnerNotApprovedStillAllowed() {
        when(userRepo.findByEmail("ou@mail.com"))
                .thenReturn(Optional.of(ownerUnapproved));
        when(passwordEncoder.matches("rawOwnerPwd", ownerUnapproved.getPassword()))
                .thenReturn(true);
        when(jwtProvider.createToken(ownerUnapproved))
                .thenReturn("jwt-owner-unapproved");

        String token = authService.login("ou@mail.com","rawOwnerPwd");

        assertThat(token).isEqualTo("jwt-owner-unapproved");
        verify(jwtProvider).createToken(ownerUnapproved);
    }

    @Test
    void testLoginOwnerApproved() {
        when(userRepo.findByEmail("oa@mail.com"))
                .thenReturn(Optional.of(ownerApproved));
        when(passwordEncoder.matches("rawOwnerPwd", ownerApproved.getPassword()))
                .thenReturn(true);
        when(jwtProvider.createToken(ownerApproved))
                .thenReturn("jwt-owner-approved");

        String token = authService.login("oa@mail.com","rawOwnerPwd");

        assertThat(token).isEqualTo("jwt-owner-approved");
        verify(jwtProvider).createToken(ownerApproved);
    }

    @Test
    void testLoginBadCredentialsThrows() {
        when(userRepo.findByEmail("x@mail.com"))
                .thenReturn(Optional.of(tenant));
        when(passwordEncoder.matches(any(),any())).thenReturn(false);

        assertThatThrownBy(() -> authService.login("x@mail.com","wrong"))
                .isInstanceOf(BadCredentialsException.class);

        verify(jwtProvider, never()).createToken(any());
    }

    @Test
    void testLogout() {
        String token = "some.jwt.token";
        authService.logout(token);
        verify(jwtProvider).invalidate(token);
    }
}
