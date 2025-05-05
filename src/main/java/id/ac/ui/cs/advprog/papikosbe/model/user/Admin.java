// src/main/java/id/ac/ui/cs/advprog/papikosbe/model/Admin.java
package id.ac.ui.cs.advprog.papikosbe.model.user;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@DiscriminatorValue("ADMIN")
@Getter
@Setter
@NoArgsConstructor
public class Admin extends User {

    /**
     * Builder constructor: memastikan email & password non-null.
     */
    @Builder
    public Admin(String email, String password) {
        Objects.requireNonNull(email,    "email must not be null");
        Objects.requireNonNull(password, "password must not be null");
        super.setEmail(email);
        super.setPassword(password);
    }
}
