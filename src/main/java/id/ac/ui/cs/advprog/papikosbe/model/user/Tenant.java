// src/main/java/id/ac/ui/cs/advprog/papikosbe/model/Tenant.java
package id.ac.ui.cs.advprog.papikosbe.model.user;
import id.ac.ui.cs.advprog.papikosbe.enums.Role;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Wallet;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@DiscriminatorValue("TENANT")
@Getter
@Setter
@NoArgsConstructor
public class Tenant extends User {

    /**
     * Builder constructor: memastikan email & password non-null.
     */
    @Builder
    public Tenant(String email, String password) {
        Objects.requireNonNull(email,    "email must not be null");
        Objects.requireNonNull(password, "password must not be null");
        super.setEmail(email);
        super.setPassword(password);
        super.setRole(Role.TENANT);       // <<< set role
    }
}
