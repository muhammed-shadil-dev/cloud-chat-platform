package com.example.chat_app.auth.controller;

import com.example.chat_app.auth.dto.RegisterRequest;
import com.example.chat_app.auth.dto.RegisterResponse;
import com.example.chat_app.auth.security.SecurityConfig;
import com.example.chat_app.auth.service.AuthService;
import com.example.chat_app.common.config.PasswordConfig;
import com.example.chat_app.common.exception.EmailAlreadyExistsException;
import com.example.chat_app.common.exception.UsernameAlreadyExistsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * HTTP-contract tests for registration. {@link AuthServiceTest} already covers the service logic;
 * this covers what only the web layer can prove - status codes, bean validation actually running,
 * the exception handler's mapping, and that the endpoint is reachable without authentication.
 *
 * <p>{@link SecurityConfig} is imported deliberately. {@code @WebMvcTest} does not pick up plain
 * {@code @Configuration} classes, so without it Spring Security's defaults would apply and every
 * request would 401 - the tests would pass or fail for reasons unrelated to the real rules. With it
 * imported, the fact that these unauthenticated requests succeed is itself the assertion that
 * {@code /api/auth/**} is public.
 */
@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, PasswordConfig.class})
class AuthControllerTest {

    private static final String VALID_BODY = """
            {
              "username": "alice",
              "email": "alice@example.com",
              "password": "password123"
            }
            """;

    @MockitoBean
    private AuthService authService;

    /**
     * Required by the imported {@link SecurityConfig}'s {@code AuthenticationManager} bean. The
     * real one loads users from the database, which this slice has no repository for - and these
     * tests only exercise the public registration path, which never authenticates anyone.
     */
    @MockitoBean
    private UserDetailsService userDetailsService;

    @Autowired
    private MockMvc mockMvc;

    // --- happy path -------------------------------------------------------------------------

    @Test
    void register_returns201WithCreatedUser_whenRequestIsValid() throws Exception {
        given(authService.register(any(RegisterRequest.class))).willReturn(
                RegisterResponse.builder()
                        .id(1L)
                        .username("alice")
                        .email("alice@example.com")
                        .message("User registered successfully")
                        .build());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.message").value("User registered successfully"));
    }

    /**
     * A password hash leaking into a response is a real and easy mistake - it happens the moment
     * someone returns the entity instead of the response DTO. Asserting its absence here means the
     * test fails immediately if that refactor ever happens.
     */
    @Test
    void register_responseNeverExposesPassword() throws Exception {
        given(authService.register(any(RegisterRequest.class))).willReturn(
                RegisterResponse.builder()
                        .id(1L)
                        .username("alice")
                        .email("alice@example.com")
                        .message("User registered successfully")
                        .build());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    void register_passesSubmittedValuesToService() throws Exception {
        given(authService.register(any(RegisterRequest.class))).willReturn(
                RegisterResponse.builder().id(1L).username("alice").email("alice@example.com").build());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated());

        ArgumentCaptor<RegisterRequest> captor = ArgumentCaptor.forClass(RegisterRequest.class);
        then(authService).should().register(captor.capture());

        RegisterRequest submitted = captor.getValue();
        assertThat(submitted.getUsername()).isEqualTo("alice");
        assertThat(submitted.getEmail()).isEqualTo("alice@example.com");
        assertThat(submitted.getPassword()).isEqualTo("password123");
    }

    // --- validation (400) -------------------------------------------------------------------

    /**
     * Asserts only that the field is reported, not the exact text: a blank username violates both
     * {@code @NotBlank} and {@code @Size(min = 3)}, and GlobalExceptionHandler keeps one message per
     * field, so which of the two survives is not deterministic.
     */
    @Test
    void register_returns400WithFieldError_whenUsernameIsBlank() throws Exception {
        String body = """
                {"username": "", "email": "alice@example.com", "password": "password123"}
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username").exists());
    }

    @Test
    void register_returns400WithFieldError_whenEmailIsMalformed() throws Exception {
        String body = """
                {"username": "alice", "email": "not-an-email", "password": "password123"}
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("Invalid email format"));
    }

    /** Default {@code @Size} message is locale-dependent, so assert the field, not the wording. */
    @Test
    void register_returns400WithFieldError_whenPasswordIsTooShort() throws Exception {
        String body = """
                {"username": "alice", "email": "alice@example.com", "password": "short"}
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").exists());
    }

    /**
     * Validation must reject the request before any business logic runs - otherwise the service
     * would be doing input checking that the boundary was supposed to have already done.
     */
    @Test
    void register_doesNotReachService_whenValidationFails() throws Exception {
        String body = """
                {"username": "", "email": "not-an-email", "password": "short"}
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        then(authService).shouldHaveNoInteractions();
    }

    // --- conflicts (409) --------------------------------------------------------------------

    @Test
    void register_returns409_whenUsernameAlreadyExists() throws Exception {
        willThrow(new UsernameAlreadyExistsException())
                .given(authService).register(any(RegisterRequest.class));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Username already exists"));
    }

    @Test
    void register_returns409_whenEmailAlreadyExists() throws Exception {
        willThrow(new EmailAlreadyExistsException())
                .given(authService).register(any(RegisterRequest.class));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Email already exists"));
    }

    /**
     * The race the application-level existsBy checks cannot close: two concurrent registrations can
     * both pass the check and both attempt the insert, and the database's unique constraint rejects
     * the loser. That must surface as a 409, not a 500.
     */
    @Test
    void register_returns409_whenDatabaseRejectsDuplicate() throws Exception {
        willThrow(new DataIntegrityViolationException("duplicate key value violates unique constraint"))
                .given(authService).register(any(RegisterRequest.class));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("A user with that username or email already exists"));
    }
}
