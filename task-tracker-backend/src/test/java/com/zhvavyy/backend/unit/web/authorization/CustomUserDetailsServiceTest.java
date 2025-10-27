package com.zhvavyy.backend.unit.web.authorization;

import com.zhvavyy.backend.model.User;
import com.zhvavyy.backend.repository.UserRepository;
import com.zhvavyy.backend.web.authorization.CustomUserDetailsService;
import com.zhvavyy.backend.unit.web.data.AuthDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static com.zhvavyy.backend.unit.web.data.AuthDataFactory.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    private User user;

    @BeforeEach
    public void init() {
        user = AuthDataFactory.createUser();
    }

    @Test
    public void loadUserByUsername() {
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername(TEST_EMAIL);
        assertEquals(userDetails.getUsername(), user.getEmail());
        assertEquals(userDetails.getPassword(), user.getPassword());
        verify(userRepository).findByEmail(TEST_EMAIL);
    }

    @Test
    public void loadUserByUsername_isNotFound() {
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(TEST_EMAIL));
    }





}
