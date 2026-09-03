//its configuration to test code with postman otherwise it returns error

package com.example.chat_app.auth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        // Probes run before a credential exists (container health
                        // checks, load balancers, Kubernetes liveness/readiness), so
                        // health must be reachable unauthenticated. Only health is
                        // exposed at all - see management.* in application.properties.
                        .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
                        .anyRequest().authenticated()
                )

                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    /**
     * Wires database-backed authentication: {@link DatabaseUserDetailsService} loads the user,
     * the BCrypt {@link PasswordEncoder} from {@code PasswordConfig} verifies the password.
     *
     * <p>Declared explicitly rather than left to auto-configuration for two reasons. It makes the
     * chain visible in one place instead of implied by which beans happen to exist, and the login
     * endpoint (P1-7) needs to inject an {@link AuthenticationManager} to authenticate credentials
     * itself.
     *
     * <p>{@code hideUserNotFoundExceptions} is left at its default of {@code true}, so an unknown
     * username surfaces as {@code BadCredentialsException} exactly like a wrong password does. A
     * caller cannot distinguish the two, which is what stops the endpoint being used to enumerate
     * accounts.
     */
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
                                                       PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(provider);
    }
}