
package id.ac.ui.cs.advprog.papikosbe.service.user;

import id.ac.ui.cs.advprog.papikosbe.exception.DuplicateEmailException;
import id.ac.ui.cs.advprog.papikosbe.model.user.Owner;
import id.ac.ui.cs.advprog.papikosbe.model.user.Tenant;
import id.ac.ui.cs.advprog.papikosbe.repository.user.OwnerRepository;
import id.ac.ui.cs.advprog.papikosbe.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepo;
    @Mock OwnerRepository ownerRepo;
    @Mock PasswordEncoder passwordEncoder;

    @InjectMocks UserServiceImpl userService;   // akan dibuat skeleton‑nya

    @BeforeEach
    void setUp() {
        when(passwordEncoder.encode(any())).thenReturn("hashedPwd"); // stub hashing
    }

    /* ---------- register tenant ---------- */

    @Test
    @DisplayName("registerTenant menyimpan tenant baru & meng‑encode password")
    void registerTenantSuccess() {
        when(userRepo.findByEmail("t@mail.com")).thenReturn(Optional.empty());

        Tenant saved = Tenant.builder().email("t@mail.com").password("hashedPwd").build();
        when(userRepo.save(any(Tenant.class))).thenReturn(saved);

        Tenant result = userService.registerTenant("t@mail.com", "pw");

        assertThat(result).isSameAs(saved);
        verify(passwordEncoder).encode("pw");
        verify(userRepo).save(any(Tenant.class));
    }

    @Test
    @DisplayName("registerTenant duplikat email -> DuplicateEmailException")
    void registerTenantDuplicateThrows() {
        when(userRepo.findByEmail("dup@mail.com")).thenReturn(Optional.of(new Tenant()));

        assertThatThrownBy(() -> userService.registerTenant("dup@mail.com", "pw"))
                .isInstanceOf(DuplicateEmailException.class);

        verify(userRepo, never()).save(any());
    }

    /* ---------- register owner ---------- */

    @Test
    @DisplayName("registerOwner menyimpan owner approved=false")
    void registerOwnerSuccess() {
        when(userRepo.findByEmail("o@mail.com")).thenReturn(Optional.empty());

        Owner persisted = Owner.builder().email("o@mail.com").password("hashedPwd").build(); // approved = false default
        when(ownerRepo.save(any(Owner.class))).thenReturn(persisted);

        Owner result = userService.registerOwner("o@mail.com", "pw");

        assertThat(result.isApproved()).isFalse();
        verify(passwordEncoder).encode("pw");
        verify(ownerRepo).save(any(Owner.class));
    }

    @Test
    @DisplayName("registerOwner duplikat email -> DuplicateEmailException")
    void registerOwnerDuplicateThrows() {
        when(userRepo.findByEmail("dup@mail.com")).thenReturn(Optional.of(new Owner()));

        assertThatThrownBy(() -> userService.registerOwner("dup@mail.com", "pw"))
                .isInstanceOf(DuplicateEmailException.class);

        verify(ownerRepo, never()).save(any());
    }
}
