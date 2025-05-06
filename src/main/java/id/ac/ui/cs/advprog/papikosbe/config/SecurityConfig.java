package id.ac.ui.cs.advprog.papikosbe.config;

import id.ac.ui.cs.advprog.papikosbe.security.JwtAuthenticationFilter;
import id.ac.ui.cs.advprog.papikosbe.security.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtProvider;

    public SecurityConfig(JwtTokenProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // Buat instance filter JWT
        JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(jwtProvider);

        http
                // 1. Stateless: tidak pakai session
                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 2. Disable CSRF karena API pakai JWT di header
                .csrf(csrf -> csrf.disable())

                // 3. CORS default (boleh disetup lebih lanjut jika perlu)
                .cors(Customizer.withDefaults())

                // 4. Atur endpoint publik vs yang butuh auth
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/owners/*/approve").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )

                // 5. Non-aktifkan form login & basic auth
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // 6. Tangani semua authentication failures dengan 401
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )

                // 7. Pasang JWT filter sebelum UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        ;

        return http.build();
    }
}
