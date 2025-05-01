package id.ac.ui.cs.advprog.papikosbe.service;

import id.ac.ui.cs.advprog.papikosbe.model.User;
import id.ac.ui.cs.advprog.papikosbe.enums.Role;

public interface UserService {
    User registerUser(String fullName, String phone, String email, String password, Role role);
    User authenticate(String email, String password);
}