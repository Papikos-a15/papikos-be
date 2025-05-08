package id.ac.ui.cs.advprog.papikosbe.repository.user;

import id.ac.ui.cs.advprog.papikosbe.model.user.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OwnerRepository extends JpaRepository<Owner, Long> {
    Optional<Owner> findByEmail(String email);
}