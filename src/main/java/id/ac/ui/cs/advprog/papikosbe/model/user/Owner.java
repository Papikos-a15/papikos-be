// src/main/java/id/ac/ui/cs/advprog/papikosbe/model/Owner.java
package id.ac.ui.cs.advprog.papikosbe.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("OWNER")
@Getter @Setter @NoArgsConstructor
public class Owner extends User {

    @Column(nullable = false)
    private boolean approved = false;

    @Builder
    public Owner(String email, String password) {
        // TODO: Objects.requireNonNull(email);
        // TODO: Objects.requireNonNull(password);
        super.setEmail(email);
        super.setPassword(password);
        this.approved = false;
    }
}
