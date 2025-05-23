// src/test/java/id/ac/ui/cs/advprog/papikosbe/repository/user/UserRepositoryTest.java
package id.ac.ui.cs.advprog.papikosbe.repository.user;

import id.ac.ui.cs.advprog.papikosbe.enums.Role;
import id.ac.ui.cs.advprog.papikosbe.model.user.Admin;
import id.ac.ui.cs.advprog.papikosbe.model.user.Owner;
import id.ac.ui.cs.advprog.papikosbe.model.user.Tenant;
import id.ac.ui.cs.advprog.papikosbe.model.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepo;

    @Test
    @DisplayName("save Tenant, Admin, Owner and find by email")
    void testSaveAndFindByEmail() {
        Tenant t = Tenant.builder()
                .email("t@mail.com")
                .password("p1").build();
        Admin a  = Admin.builder()
                .email("a@mail.com")
                .password("p2").build();
        Owner o  = Owner.builder()
                .email("o@mail.com")
                .password("p3").build();

        userRepo.saveAll(List.of(t, a, o));

        Optional<User> foundT = userRepo.findByEmail("t@mail.com");
        Optional<User> foundA = userRepo.findByEmail("a@mail.com");
        Optional<User> foundO = userRepo.findByEmail("o@mail.com");

        assertThat(foundT).isPresent().containsInstanceOf(Tenant.class);
        assertThat(foundA).isPresent().containsInstanceOf(Admin.class);
        assertThat(foundO).isPresent().containsInstanceOf(Owner.class);
    }

    @Test
    @DisplayName("findByEmail returns empty when no such user")
    void testFindByEmailReturnsEmpty() {
        Optional<User> none = userRepo.findByEmail("notfound@mail.com");
        assertThat(none).isEmpty();
    }

    @Test
    @DisplayName("duplicate email should throw DataIntegrityViolationException")
    void testDuplicateEmailThrows() {
        Tenant t1 = Tenant.builder()
                .email("dup@mail.com")
                .password("p").build();
        Tenant t2 = Tenant.builder()
                .email("dup@mail.com")
                .password("q").build();

        userRepo.saveAndFlush(t1);
        assertThatThrownBy(() -> userRepo.saveAndFlush(t2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("findById returns user when exists")
    void testFindByIdReturnsUser() {
        Tenant tenant = Tenant.builder()
                .email("user123@mail.com")
                .password("securepass").build();

        User saved = userRepo.saveAndFlush(tenant);

        Optional<User> found = userRepo.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("user123@mail.com");
        assertThat(found.get()).isInstanceOf(Tenant.class);
    }
}
