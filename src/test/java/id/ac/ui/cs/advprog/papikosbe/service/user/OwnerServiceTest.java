// src/test/java/id/ac/ui/cs/advprog/papikosbe/service/OwnerServiceTest.java
package id.ac.ui.cs.advprog.papikosbe.service.user;

import id.ac.ui.cs.advprog.papikosbe.model.user.Owner;
import id.ac.ui.cs.advprog.papikosbe.repository.user.OwnerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import jakarta.persistence.EntityNotFoundException;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerServiceTest {

    @Mock OwnerRepository ownerRepo;
    @InjectMocks OwnerService ownerService;

    @Test
    void testApproveOwnerSuccess() {
        Owner o = Owner.builder()
                .email("o@mail.com")
                .password("pwd")
                .build();
        o.setId(42L);
        when(ownerRepo.findById(42L)).thenReturn(Optional.of(o));
        when(ownerRepo.save(o)).thenReturn(o);

        Owner approved = ownerService.approve(42L);

        assertThat(approved.isApproved()).isTrue();
        verify(ownerRepo).save(o);
    }

    @Test
    void testApproveOwnerNotFoundThrows() {
        when(ownerRepo.findById(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ownerService.approve(7L))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
