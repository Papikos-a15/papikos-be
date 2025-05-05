// src/main/java/id/ac/ui/cs/advprog/papikosbe/model/Admin.java
package id.ac.ui.cs.advprog.papikosbe.model.user;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("ADMIN")
@Getter @Setter @NoArgsConstructor
public class Admin extends User {

    /**
     * Builder constructor, akan dipanggil oleh Lombok-generated builder()
     * • validasi non-null di sini
     */
    @Builder
    public Admin(String email, String password) {
        // TODO: Objects.requireNonNull(email);
        // TODO: Objects.requireNonNull(password);
        super.setEmail(email);
        super.setPassword(password);
    }
}
