package id.ac.ui.cs.advprog.papikosbe.service.user;

import id.ac.ui.cs.advprog.papikosbe.model.user.Owner;
import id.ac.ui.cs.advprog.papikosbe.model.user.Tenant;

import java.util.UUID;

public interface UserService {
    Tenant registerTenant(String email, String rawPassword);
    Owner  registerOwner (String email, String rawPassword);
    String getEmailById(UUID id);
}
