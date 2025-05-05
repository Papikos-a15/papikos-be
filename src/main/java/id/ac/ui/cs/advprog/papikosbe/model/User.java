package id.ac.ui.cs.advprog.papikosbe.model;
import id.ac.ui.cs.advprog.papikosbe.enums.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean isApproved;

    public User(String email, String password, Role role) {

    }

}

