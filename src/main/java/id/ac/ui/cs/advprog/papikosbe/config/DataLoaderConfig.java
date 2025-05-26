package id.ac.ui.cs.advprog.papikosbe.config;

import id.ac.ui.cs.advprog.papikosbe.enums.Role;
import id.ac.ui.cs.advprog.papikosbe.model.user.Admin;
import id.ac.ui.cs.advprog.papikosbe.model.user.User;
import id.ac.ui.cs.advprog.papikosbe.repository.user.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.CommandLineRunner;

@Configuration
public class DataLoaderConfig {

    @Bean
    public CommandLineRunner loadData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByEmail("admin1@example.com").isEmpty()) {
                User admin = new Admin();
                admin.setEmail("admin1@example.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);
            }
        };
    }
}
