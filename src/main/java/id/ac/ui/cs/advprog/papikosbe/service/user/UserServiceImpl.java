// src/main/java/id/ac/ui/cs/advprog/papikosbe/service/user/UserServiceImpl.java
package id.ac.ui.cs.advprog.papikosbe.service.user;

import id.ac.ui.cs.advprog.papikosbe.exception.DuplicateEmailException;
import id.ac.ui.cs.advprog.papikosbe.model.user.Owner;
import id.ac.ui.cs.advprog.papikosbe.model.user.Tenant;
import id.ac.ui.cs.advprog.papikosbe.repository.user.OwnerRepository;
import id.ac.ui.cs.advprog.papikosbe.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final OwnerRepository ownerRepo;
    private final PasswordEncoder passwordEncoder;

    /* -------------------------------------------------
     * Register Tenant
     * ------------------------------------------------- */
    @Override
    @Transactional
    public Tenant registerTenant(String email, String rawPassword) {
        ensureEmailUnique(email);

        String hash = passwordEncoder.encode(rawPassword);

        Tenant tenant = Tenant.builder()
                .email(email)
                .password(hash)
                .build();

        return userRepo.save(tenant);
    }

    /* -------------------------------------------------
     * Register Owner (approved = false by default)
     * ------------------------------------------------- */
    @Override
    @Transactional
    public Owner registerOwner(String email, String rawPassword) {
        ensureEmailUnique(email);

        String hash = passwordEncoder.encode(rawPassword);

        Owner owner = Owner.builder()
                .email(email)
                .password(hash)
                .build();           // approved = false by default

        return ownerRepo.save(owner);
    }

    @Override
    public String getEmailById(UUID id) {
        return userRepo.findById(id)
                .map(user -> user.getEmail())
                .orElse("tidak diketahui");
    }

    /* ---------- util ---------- */
    private void ensureEmailUnique(String email) {
        if (userRepo.findByEmail(email).isPresent()) {
            throw new DuplicateEmailException(email);
        }
    }
}
