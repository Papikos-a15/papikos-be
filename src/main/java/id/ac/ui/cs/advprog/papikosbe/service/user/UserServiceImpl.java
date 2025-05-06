// src/main/java/id/ac/ui/cs/advprog/papikosbe/service/user/UserServiceImpl.java
package id.ac.ui.cs.advprog.papikosbe.service.user;

import id.ac.ui.cs.advprog.papikosbe.model.user.Owner;
import id.ac.ui.cs.advprog.papikosbe.model.user.Tenant;
import id.ac.ui.cs.advprog.papikosbe.repository.user.OwnerRepository;
import id.ac.ui.cs.advprog.papikosbe.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final OwnerRepository ownerRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Tenant registerTenant(String email, String rawPassword) {
        // TODO: implementasi agar lulus test
        throw new UnsupportedOperationException("registerTenant not yet implemented");
    }

    @Override
    public Owner registerOwner(String email, String rawPassword) {
        // TODO: implementasi agar lulus test
        throw new UnsupportedOperationException("registerOwner not yet implemented");
    }
}
