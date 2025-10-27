package com.zhvavyy.backend.integration.authorization.service;

import com.zhvavyy.backend.integration.BaseIntegrationTest;
import com.zhvavyy.backend.kafka.messaging.dto.MessageForEmail;
import com.zhvavyy.backend.kafka.messaging.producer.RegisterProducer;
import com.zhvavyy.backend.model.User;
import com.zhvavyy.backend.model.enums.Role;
import com.zhvavyy.backend.repository.UserRepository;
import com.zhvavyy.backend.web.authorization.AuthService;
import com.zhvavyy.backend.web.dto.JwtResponse;
import com.zhvavyy.backend.web.dto.LoginRequest;
import com.zhvavyy.backend.web.dto.RegisterRequest;
import com.zhvavyy.backend.web.handler.PasswordIncorrectException;
import com.zhvavyy.backend.web.handler.UsernameAlreadyException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@AutoConfigureMockMvc
@DisplayName("Integration test for AuthService")
public class AuthServiceIT extends BaseIntegrationTest {

    @Autowired
    private AuthService authService;
    @MockitoBean
    private RegisterProducer producer;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private RegisterRequest registerRequest;

    private static final String TEST_EMAIL = "new@gmail.com";
    private static final String TEST_FNAME = "firstName";
    private static final String TEST_LNAME = "LastName";
    private static final String TEST_PASS = "tests23";

    @BeforeEach
    public void init() {
        userRepository.deleteAll();
        registerRequest = new RegisterRequest();
        registerRequest.setEmail(TEST_EMAIL);
        registerRequest.setPassword(TEST_PASS);
        registerRequest.setFirstname(TEST_FNAME);
        registerRequest.setLastname(TEST_LNAME);
        registerRequest.setConfirmPassword(TEST_PASS);
    }

    @Test
    @DisplayName("Registration success - user created and email sent")
    public void registration_whenSuccess() {
        doNothing().when(producer).sendMessage(any(MessageForEmail.class));

        JwtResponse register = authService.register(registerRequest);
        Optional<User> shouldUser = userRepository.findByEmail(registerRequest.getEmail());

        User actualUser = shouldUser.filter(u -> u.getEmail().equals(registerRequest.getEmail())).stream().findFirst().orElseThrow();
        verify(producer, times(1)).sendMessage(any(MessageForEmail.class));

        verify(producer).sendMessage(argThat(message ->
                message.getRecipient().equals(registerRequest.getEmail()) &&
                        message.getSubject().equals("Successful registration in Task Tracker") &&
                        message.getMsgBody().contains(registerRequest.getFirstname())
        ));
        assertAll(
                () -> assertNotNull(register),
                () -> assertEquals(registerRequest.getEmail(), actualUser.getEmail()),
                () -> assertTrue(passwordEncoder.matches(registerRequest.getPassword(), actualUser.getPassword())),
                () -> assertNotEquals(TEST_PASS, actualUser.getPassword())
        );
    }

    @Test
    @DisplayName("Registration failure - email already exists")
    public void shouldException_whenEmailAlreadyExist() {
        userRepository.save(User.builder()
                .email(TEST_EMAIL)
                .role(Role.USER)
                .firstname(TEST_FNAME)
                .lastname(TEST_LNAME)
                .password(passwordEncoder.encode(TEST_PASS))
                .build());

        assertThrows(UsernameAlreadyException.class, () -> authService.register(registerRequest));
    }

    @Test
    @DisplayName("Registration failure - passwords do not match")
    public void shouldException_whenPasswordNotMatch() {
        registerRequest.setConfirmPassword("wrongPass");
        assertThrows(PasswordIncorrectException.class, () -> authService.register(registerRequest));
    }

    @Test
    @DisplayName("Authentication success - user logged in successfully")
    public void authenticate_success() {
        userRepository.save(User.builder()
                .email(TEST_EMAIL)
                .role(Role.USER)
                .firstname(TEST_FNAME)
                .lastname(TEST_LNAME)
                .password(passwordEncoder.encode(TEST_PASS))
                .build());
        userRepository.flush();
        LoginRequest loginRequest = new LoginRequest(TEST_EMAIL, TEST_PASS);
        JwtResponse response = authService.authenticate(loginRequest);

        Optional<User> user = userRepository.findByEmail(TEST_EMAIL);

        assertAll(
                () -> assertNotNull(response),
                () -> assertTrue(user.isPresent()),
                () -> assertTrue(passwordEncoder.matches(TEST_PASS, user.get().getPassword()))
        );
    }

    @AfterEach
    public void clean() {
        userRepository.deleteAll();
    }
}
