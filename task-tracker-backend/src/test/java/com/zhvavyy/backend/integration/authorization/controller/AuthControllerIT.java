package com.zhvavyy.backend.integration.authorization.controller;

import com.zhvavyy.backend.integration.BaseIntegrationTest;
import com.zhvavyy.backend.model.User;
import com.zhvavyy.backend.model.enums.Role;
import com.zhvavyy.backend.repository.UserRepository;
import com.zhvavyy.backend.web.dto.RegisterRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerIT extends BaseIntegrationTest {


    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MockMvc mockMvc;
    private static final String BASE_URL = "/auth";
    private static final String TEST_EMAIL = "email@gmail.com";
    private static final String TEST_PASS = "secret";
    private static final String TEST_TOKEN = "secretToken";
    private static final String TEST_FNAME = "firstName";
    private static final String TEST_LNAME = "LastName";
    @Autowired
    private PasswordEncoder passwordEncoder;


    @BeforeEach
    public void init() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail(TEST_EMAIL);
        registerRequest.setPassword(TEST_PASS);
        registerRequest.setFirstname(TEST_FNAME);
        registerRequest.setLastname(TEST_LNAME);
        registerRequest.setConfirmPassword(TEST_PASS);
    }

    @Test
    public void shouldSuccessRegistration() throws Exception {

        mockMvc.perform(post(BASE_URL + "/signup")
                        .content("""
                                {
                                "email": "email@gmail.com",
                                "password": "secret",
                                "confirmPassword": "secret",
                                "firstname": "firsts",
                                "lastname": "lasts",
                                "role": "USER"
                                }
                                """)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwtToken").exists());
    }

    @Test
    @DisplayName("Registration failure - email already exists")
    public void shouldFailWhenEmailAlreadyExists() throws Exception {
        userRepository.save(User.builder()
                .email(TEST_EMAIL)
                .firstname(TEST_FNAME)
                .lastname(TEST_LNAME)
                .password(passwordEncoder.encode(TEST_PASS))
                .role(Role.USER)
                .build());

        mockMvc.perform(post(BASE_URL + "/signup")
                        .content("""
                                {
                                "email": "email@gmail.com",
                                "password": "secret",
                                "confirmPassword": "secret",
                                "firstname": "firstName",
                                "lastname": "LastName",
                                "role": "USER"
                                }
                                """)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("This email is already taken"));
    }

    @Test
    @DisplayName("Registration failure - password is not match")
    public void shouldFail_whenPasswordNotMatch() throws Exception {
        mockMvc.perform(post(BASE_URL + "/signup")
                        .content("""
                                {
                                "email": "email@gmail.com",
                                "password": "secret",
                                "confirmPassword": "wrongSecret",
                                "firstname": "firstName",
                                "lastname": "LastName",
                                "role": "USER"
                                }
                                """)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Passwords do not match"));
    }

    @Test
    public void shouldLoginAuth_success() throws Exception {
        userRepository.save(User.builder()
                .email(TEST_EMAIL)
                .firstname(TEST_FNAME)
                .lastname(TEST_LNAME)
                .password(passwordEncoder.encode(TEST_PASS))
                .role(Role.USER)
                .build());


        mockMvc.perform(post(BASE_URL + "/login")
                        .content("""
                                {
                                "email": "email@gmail.com",
                                "rawPassword": "secret"
                                }
                                """)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwtToken").exists());
    }

    @AfterEach
    public void clean(){
        userRepository.deleteAll();
    }
}
