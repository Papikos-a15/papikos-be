// src/main/java/id/ac/ui/cs/advprog/papikosbe/config/SecurityConfig.java
package id.ac.ui.cs.advprog.papikosbe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity          // untuk @PreAuthorize dsb.
public class SecurityConfig {

    /* ------------------------------------------------------------------
     * Password hashing
     * ------------------------------------------------------------------ */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /* ------------------------------------------------------------------
     * HTTP‑security rules
     * ------------------------------------------------------------------ */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                /* 1. API stateless → tidak pakai session */
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                /* 2. CSRF tidak diperlukan karena autentikasi via JWT header */
                .csrf(csrf -> csrf.disable())

                /* 3. (Opsional) CORS: izinkan frontend lain origin */
                .cors(Customizer.withDefaults())

                /* 4. Autorisasi endpoint */
                .authorizeHttpRequests(auth -> auth
                        // register, login, logout bebas
                        .requestMatchers("/auth/**").permitAll()
                        // approve owner hanya ADMIN (ganti jika perlu)
                        .requestMatchers(HttpMethod.PATCH, "/owners/*/approve").hasRole("ADMIN")
                        // endpoint lain butuh autentikasi
                        .anyRequest().authenticated())

                /* 5. Matikan mekanisme form‑login & basic auth default */
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        /* 6. Tambahkan filter JWT milik Anda di sini
              (mis. http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)); */

        return http.build();
    }
}
