// src/test/java/id/ac/ui/cs/advprog/papikosbe/service/OwnerServiceTest.java
package id.ac.ui.cs.advprog.papikosbe.service.user;

import id.ac.ui.cs.advprog.papikosbe.model.user.Owner;
import id.ac.ui.cs.advprog.papikosbe.repository.user.OwnerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import jakarta.persistence.EntityNotFoundException;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
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
    void testFindUnapprovedOwners() {
        // Simulasikan owner yang sudah disetujui
        Owner approvedOwner = Owner.builder()
                .email("approved@x.com")
                .password("p")
                .build();
        approvedOwner.setApproved(true);  // Set approved ke true

        // Simulasikan owner yang belum disetujui
        Owner unapprovedOwner1 = Owner.builder()
                .email("unapproved1@x.com")
                .password("p")
                .build();
        unapprovedOwner1.setApproved(false);  // Set approved ke false

        Owner unapprovedOwner2 = Owner.builder()
                .email("unapproved2@x.com")
                .password("p")
                .build();
        unapprovedOwner2.setApproved(false);  // Set approved ke false

        // Simulasi owner yang ada di repository
        when(ownerRepo.findByApprovedFalse()).thenReturn(List.of(unapprovedOwner1, unapprovedOwner2));

        // Ambil semua owner yang belum disetujui
        var results = ownerService.findUnapprovedOwners();

        // Verifikasi bahwa hanya owner yang belum disetujui yang muncul
        assertThat(results).hasSize(2);
        assertThat(results).extracting("email")
                .containsExactlyInAnyOrder("unapproved1@x.com", "unapproved2@x.com");
    }

    @Test
    void testFindUnapprovedOwnersReturnsEmpty() {
        // Owner yang sudah disetujui
        Owner approvedOwner = Owner.builder()
                .email("approved@x.com")
                .password("p")
                .build();

        approvedOwner.setApproved(true); // Set approved ke true (owner sudah disetujui)

        // Simulasikan owner yang disimpan dalam repository
        when(ownerRepo.findByApprovedFalse()).thenReturn(List.of());

        // Ambil semua owner yang belum disetujui
        var results = ownerService.findUnapprovedOwners();

        // Verifikasi bahwa tidak ada owner yang belum disetujui
        assertThat(results).isEmpty();
    }

    @Test
    void testFindOwnerByIdSuccess() {
        // Setup test data
        UUID id = UUID.randomUUID();
        Owner expectedOwner = Owner.builder()
                .email("owner@example.com")
                .password("password")
                .build();
        expectedOwner.setId(id);

        // Configure mock repository to return the test data
        when(ownerRepo.findById(id)).thenReturn(Optional.of(expectedOwner));

        // Call the service method
        Owner result = ownerService.findOwnerById(id);

        // Verify the result
        assertThat(result).isEqualTo(expectedOwner);
        assertThat(result.getEmail()).isEqualTo("owner@example.com");
        verify(ownerRepo).findById(id);
    }

    @Test
    void testFindOwnerByIdNotFound() {
        // Setup test data
        UUID id = UUID.randomUUID();

        // Configure mock repository to return empty
        when(ownerRepo.findById(id)).thenReturn(Optional.empty());

        // Verify that the service throws EntityNotFoundException
        assertThatThrownBy(() -> ownerService.findOwnerById(id))
                .isInstanceOf(EntityNotFoundException.class);

        verify(ownerRepo).findById(id);
    }

    @Test
    void testApproveAlreadyApprovedOwnerDoesNotResave() {
        UUID id = UUID.randomUUID();
        Owner alreadyApproved = Owner.builder()
                .email("already@approved.com")
                .password("p")
                .build();
        alreadyApproved.setId(id);
        alreadyApproved.setApproved(true); // Sudah disetujui

        when(ownerRepo.findById(id)).thenReturn(Optional.of(alreadyApproved));

        Owner result = ownerService.approve(id);

        assertThat(result.isApproved()).isTrue();
        verify(ownerRepo, never()).save(any()); // Tidak ada simpan ulang
    }
}
