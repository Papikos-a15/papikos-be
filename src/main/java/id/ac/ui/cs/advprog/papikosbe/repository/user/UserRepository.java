package id.ac.ui.cs.advprog.papikosbe.repository.user;

import id.ac.ui.cs.advprog.papikosbe.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
