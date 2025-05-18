package id.ac.ui.cs.advprog.papikosbe.repository.user;

import id.ac.ui.cs.advprog.papikosbe.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
}
