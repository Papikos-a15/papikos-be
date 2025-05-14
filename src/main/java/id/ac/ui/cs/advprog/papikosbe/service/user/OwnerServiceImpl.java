// src/main/java/id/ac/ui/cs/advprog/papikosbe/service/user/OwnerServiceImpl.java
package id.ac.ui.cs.advprog.papikosbe.service.user;

import id.ac.ui.cs.advprog.papikosbe.model.user.Owner;
import id.ac.ui.cs.advprog.papikosbe.repository.user.OwnerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.UUID;

@Service
public class OwnerServiceImpl implements OwnerService {

    private final OwnerRepository ownerRepo;

    public OwnerServiceImpl(OwnerRepository ownerRepo) {
        this.ownerRepo = ownerRepo;
    }

    /**
     * Menyetujui (approve) pemilik kos dengan ID tertentu.
     * Jika owner tidak ditemukan, lempar EntityNotFoundException.
     */
    @Override
    @Transactional
    public Owner approve(UUID ownerId) {
        // 1. Ambil owner, atau lempar exception jika tidak ada
        Owner owner = ownerRepo.findById(ownerId)
                .orElseThrow(EntityNotFoundException::new);

        // 2. Set approved = true jika belum
        if (!owner.isApproved()) {
            owner.setApproved(true);
            owner = ownerRepo.save(owner);   // simpan perubahan
        }
        // 3. Kembalikan owner yang sudah disetujui
        return owner;
    }
    /**
     * Mengambil daftar owner yang belum disetujui (approved = false).
     * @return daftar owner yang belum disetujui
     */
    @Override
    public List<Owner> findUnapprovedOwners() {
        // Mengambil daftar owner yang belum disetujui (approved = false)
        return ownerRepo.findByApprovedFalse();
    }
}
