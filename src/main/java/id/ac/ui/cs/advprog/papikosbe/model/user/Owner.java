// src/main/java/id/ac/ui/cs/advprog/papikosbe/model/Owner.java
package id.ac.ui.cs.advprog.papikosbe.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@DiscriminatorValue("OWNER")
@Getter
@Setter
@NoArgsConstructor
public class Owner extends User {

    @Column(nullable = false)
    private boolean approved = false;

    /**
     * Builder constructor: memastikan email & password non-null,
     * dan default approved=false.
     */
    @Builder
    public Owner(String email, String password) {
        Objects.requireNonNull(email,    "email must not be null");
        Objects.requireNonNull(password, "password must not be null");
        super.setEmail(email);
        super.setPassword(password);
        this.approved = false;
    }
}
