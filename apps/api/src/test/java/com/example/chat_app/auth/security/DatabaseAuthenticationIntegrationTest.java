package com.example.chat_app.auth.security;

import com.example.chat_app.users.entity.User;
import com.example.chat_app.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The test that actually proves P1-6: a user stored in the database can authenticate.
 *
 * <p>Before this task the application had no {@code UserDetailsService}, so Spring Boot fell back to
 * {@code inMemoryUserDetailsManager} and every registered account was unusable. The unit tests cover
 * the pieces; this exercises the real filter chain, the real {@code DaoAuthenticationProvider}, and
 * the real BCrypt encoder end to end.
 *
 * <p>The probe path is {@code /actuator/env}, which is deliberately <em>not</em> exposed
 * ({@code management.endpoints.web.exposure.include=health}). That makes the two outcomes
 * unambiguous: <b>401</b> means authentication failed, <b>404</b> means it succeeded and the request
 * reached routing. There is no protected business endpoint to point at yet - the first one arrives
 * with P2-1.
 */
@SpringBootTest(properties = {
        "spring.autoconfigure.exclude="
                + "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,"
                + "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration,"
                + "org.springframework.boot.data.jpa.autoconfigure.DataJpaRepositoriesAutoConfiguration"
})
@AutoConfigureMockMvc
class DatabaseAuthenticationIntegrationTest {

    private static final String PROTECTED_PATH = "/actuator/env";
    private static final String RAW_PASSWORD = "password123";

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User storedUser(boolean enabled) {
        return User.builder()
                .id(1L)
                .username("alice")
                .email("alice@example.com")
                .password(passwordEncoder.encode(RAW_PASSWORD))
                .enabled(enabled)
                .build();
    }

    private static String basic(String identifier, String password) {
        String credentials = identifier + ":" + password;
        return "Basic " + Base64.getEncoder()
                .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void databaseUserAuthenticatesWithUsername() throws Exception {
        given(userRepository.findByUsername("alice")).willReturn(Optional.of(storedUser(true)));

        mockMvc.perform(get(PROTECTED_PATH)
                        .header(HttpHeaders.AUTHORIZATION, basic("alice", RAW_PASSWORD)))
                .andExpect(status().isNotFound());
    }

    @Test
    void databaseUserAuthenticatesWithEmail() throws Exception {
        given(userRepository.findByUsername("alice@example.com")).willReturn(Optional.empty());
        given(userRepository.findByEmail("alice@example.com")).willReturn(Optional.of(storedUser(true)));

        mockMvc.perform(get(PROTECTED_PATH)
                        .header(HttpHeaders.AUTHORIZATION, basic("alice@example.com", RAW_PASSWORD)))
                .andExpect(status().isNotFound());
    }

    /** Proves the stored value is treated as a BCrypt hash and actually compared, not echoed back. */
    @Test
    void wrongPasswordIsRejected() throws Exception {
        given(userRepository.findByUsername("alice")).willReturn(Optional.of(storedUser(true)));

        mockMvc.perform(get(PROTECTED_PATH)
                        .header(HttpHeaders.AUTHORIZATION, basic("alice", "wrong-password")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void unknownUserIsRejected() throws Exception {
        given(userRepository.findByUsername("ghost")).willReturn(Optional.empty());
        given(userRepository.findByEmail("ghost")).willReturn(Optional.empty());

        mockMvc.perform(get(PROTECTED_PATH)
                        .header(HttpHeaders.AUTHORIZATION, basic("ghost", RAW_PASSWORD)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void missingCredentialsAreRejected() throws Exception {
        mockMvc.perform(get(PROTECTED_PATH))
                .andExpect(status().isUnauthorized());
    }

    /** A disabled account must not authenticate even with the correct password. */
    @Test
    void disabledUserIsRejected() throws Exception {
        given(userRepository.findByUsername("alice")).willReturn(Optional.of(storedUser(false)));

        mockMvc.perform(get(PROTECTED_PATH)
                        .header(HttpHeaders.AUTHORIZATION, basic("alice", RAW_PASSWORD)))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Wiring up authentication must not have locked down the paths that were public. Registration is
     * checked with a GET, which is the wrong method for it: a <b>405</b> proves the request passed
     * security and reached routing, whereas a 401 would mean the endpoint had been closed off.
     */
    @Test
    void publicPathsStayReachableWithoutCredentials() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/auth/register"))
                .andExpect(status().isMethodNotAllowed());
    }
}
