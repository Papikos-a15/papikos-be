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
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerServiceTest {

    @Mock OwnerRepository ownerRepo;
    @InjectMocks OwnerServiceImpl ownerService;

    @Test
    void testApproveOwnerSuccess() {
        Owner o = Owner.builder()
                .email("o@mail.com")
                .password("pwd")
                .build();
        UUID id = UUID.randomUUID();
        o.setId(id);
        when(ownerRepo.findById(id)).thenReturn(Optional.of(o));
        when(ownerRepo.save(o)).thenReturn(o);

        Owner approved = ownerService.approve(id);

        assertThat(approved.isApproved()).isTrue();
        verify(ownerRepo).save(o);
    }

    @Test
    void testApproveOwnerNotFoundThrows() {
        UUID id = UUID.randomUUID();
        when(ownerRepo.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ownerService.approve(id))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void testFindByApprovedFalse() {
        // Owner yang sudah disetujui
        Owner approvedOwner = Owner.builder()
                .email("approved@x.com")
                .password("p")
                .build();

        // Owner yang belum disetujui
        Owner unapprovedOwner1 = Owner.builder()
                .email("unapproved1@x.com")
                .password("p")
                .build();

        Owner unapprovedOwner2 = Owner.builder()
                .email("unapproved2@x.com")
                .password("p")
                .build();

        approvedOwner.setApproved(true);

        // Simulasikan owner disimpan dalam database
        ownerRepo.saveAndFlush(approvedOwner);
        ownerRepo.saveAndFlush(unapprovedOwner1);
        ownerRepo.saveAndFlush(unapprovedOwner2);

        // Ambil semua owner yang belum disetujui
        var results = ownerService.findByApprovedFalse();

        // Verifikasi bahwa hanya owner yang belum disetujui yang muncul
        assertThat(results).hasSize(2);
        assertThat(results).extracting("email")
                .containsExactlyInAnyOrder("unapproved1@x.com", "unapproved2@x.com");
    }

    @Test
    void testFindByApprovedFalseReturnsEmpty() {
        // Owner yang sudah disetujui
        Owner approvedOwner = Owner.builder()
                .email("approved@x.com")
                .password("p")
                .build();

        approvedOwner.setApproved(true);

        // Simulasikan owner disimpan dalam database
        ownerRepo.saveAndFlush(approvedOwner);

        // Ambil semua owner yang belum disetujui
        var results = ownerService.findByApprovedFalse();

        // Verifikasi bahwa tidak ada owner yang belum disetujui
        assertThat(results).isEmpty();
    }
}
