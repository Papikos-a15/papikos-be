// src/main/java/id/ac/ui/cs/advprog/papikosbe/model/Tenant.java
package id.ac.ui.cs.advprog.papikosbe.model.user;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("TENANT")
@Getter @Setter @NoArgsConstructor
public class Tenant extends User {

    @Builder
    public Tenant(String email, String password) {
        // TODO: Objects.requireNonNull(email);
        // TODO: Objects.requireNonNull(password);
        super.setEmail(email);
        super.setPassword(password);
    }
}
