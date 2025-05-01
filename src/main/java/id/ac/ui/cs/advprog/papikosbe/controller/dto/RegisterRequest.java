package id.ac.ui.cs.advprog.papikosbe.controller.dto;

import id.ac.ui.cs.advprog.papikosbe.enums.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String fullName;
    private String phone;
    private String email;
    private String password;
    private Role role;
}