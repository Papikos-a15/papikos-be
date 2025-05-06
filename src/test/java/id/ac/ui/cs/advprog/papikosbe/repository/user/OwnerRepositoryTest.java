// src/test/java/id/ac/ui/cs/advprog/papikosbe/repository/user/OwnerRepositoryTest.java
package id.ac.ui.cs.advprog.papikosbe.repository.user;

import id.ac.ui.cs.advprog.papikosbe.model.user.Owner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import jakarta.persistence.PersistenceException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class OwnerRepositoryTest {

    @Autowired
    private OwnerRepository ownerRepo;

    @Test
    @DisplayName("save Owner defaults approved=false and findById")
    void testSaveOwnerDefaultsApprovedFalse() {
        Owner o = Owner.builder()
                .email("owner@x.com")
                .password("pwd")
                .build();
        Owner saved = ownerRepo.save(o);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.isApproved()).isFalse();

        Optional<Owner> loaded = ownerRepo.findById(saved.getId());
        assertThat(loaded).isPresent()
                .get()
                .extracting(Owner::isApproved)
                .isEqualTo(false);
    }

    @Test
    @DisplayName("update approved flag and persist change")
    void testUpdateApprovedFlag() {
        Owner o = Owner.builder()
                .email("owner2@x.com")
                .password("pwd")
                .build();
        Owner saved = ownerRepo.save(o);

        saved.setApproved(true);
        ownerRepo.saveAndFlush(saved);

        Optional<Owner> reloaded = ownerRepo.findById(saved.getId());
        assertThat(reloaded).isPresent()
                .get()
                .extracting(Owner::isApproved)
                .isEqualTo(true);
    }

    @Test
    @DisplayName("findByEmail returns Owner when exists")
    void testFindByEmailReturnsOwner() {
        Owner o = Owner.builder()
                .email("owner@find.com")
                .password("pwd")
                .build();
        Owner saved = ownerRepo.saveAndFlush(o);

        Optional<Owner> found = ownerRepo.findByEmail("owner@find.com");
        assertThat(found)
                .isPresent()
                .get()
                .isEqualTo(saved);
    }

    @Test
    @DisplayName("findByEmail returns empty when not found")
    void testFindByEmailReturnsEmpty() {
        Optional<Owner> found = ownerRepo.findByEmail("no-such@owner.com");
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("duplicate email among owners should throw PersistenceException")
    void testDuplicateEmailThrowsForOwners() {
        Owner o1 = Owner.builder()
                .email("dup@owner.com")
                .password("p").build();
        Owner o2 = Owner.builder()
                .email("dup@owner.com")
                .password("q").build();

        ownerRepo.saveAndFlush(o1);

        // ini akan gagal karena UNIQUE constraint pada kolom email di tabel users
        assertThatThrownBy(() -> ownerRepo.saveAndFlush(o2))
                .isInstanceOf(PersistenceException.class);
    }

}
