package com.zhvavyy.backend.integration.controller;

import com.zhvavyy.backend.dto.CurrentUserDto;
import com.zhvavyy.backend.integration.BaseIntegrationTest;
import com.zhvavyy.backend.model.User;
import com.zhvavyy.backend.model.enums.Role;
import com.zhvavyy.backend.service.UserService;
import com.zhvavyy.backend.web.security.details.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIT extends BaseIntegrationTest {

    @MockitoBean
    private UserService userService;
    private CustomUserDetails customUserDetails;
    @Autowired
    private MockMvc mockMvc;

    private static final String TEST_EMAIL = "user@gmail.com";
    private static final String BASE_URL = "/app/v1/users";

    @BeforeEach
    void init() {
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(4L);
        when(mockUser.getEmail()).thenReturn(TEST_EMAIL);
        when(mockUser.getRole()).thenReturn(Role.USER);
        customUserDetails = new CustomUserDetails(mockUser);
    }


    @Test
    @DisplayName("UserController integration tests: when Authorized")
    public void shouldReturnCurrentUser_whenAuthorized() throws Exception {
        CurrentUserDto currentUser = new CurrentUserDto(4L, TEST_EMAIL, Role.USER);
        when(userService.getUser(customUserDetails)).thenReturn(currentUser);

        mockMvc.perform(get(BASE_URL + "/current")
                        .with(user(customUserDetails))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.id").value(4L))
                .andExpect(jsonPath("$.role").value(Role.USER.toString()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }

    @Test
    @DisplayName("UserController integration tests: when Unauthorized")
    void shouldReturnForbidden_whenUnauthorized() throws Exception {
        mockMvc.perform(get(BASE_URL + "/current"))
                .andExpect(status().isForbidden());
    }

}
