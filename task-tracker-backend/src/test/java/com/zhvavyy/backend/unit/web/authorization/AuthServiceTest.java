package com.zhvavyy.backend.unit.web.authorization;

import com.zhvavyy.backend.kafka.messaging.dto.MessageForEmail;
import com.zhvavyy.backend.kafka.messaging.producer.RegisterProducer;
import com.zhvavyy.backend.model.User;
import com.zhvavyy.backend.repository.UserRepository;
import com.zhvavyy.backend.web.authorization.AuthService;
import com.zhvavyy.backend.web.dto.JwtResponse;
import com.zhvavyy.backend.web.dto.LoginRequest;
import com.zhvavyy.backend.web.dto.RegisterRequest;
import com.zhvavyy.backend.web.handler.PasswordIncorrectException;
import com.zhvavyy.backend.web.handler.UsernameAlreadyException;
import com.zhvavyy.backend.web.jwt.JwtUtils;
import com.zhvavyy.backend.web.security.details.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.zhvavyy.backend.unit.web.data.AuthDataFactory.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("FieldCanBeLocal")
@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private RegisterProducer producer;
    @Mock
    private Authentication authentication;
    @Mock
    private CustomUserDetails customUserDetails;
  
    private final String PASSWORD_ENCODE= "#123qwerty";
    private final String JWT_TOKEN= "TokEnJwT";
    private User user;
    private RegisterRequest registerRequest;
    
    @BeforeEach
    public void  init(){
        user = createUser();
        registerRequest= createRegisterRequest();
    }
    
    @Test
    public void registration(){
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn(PASSWORD_ENCODE);
        when(jwtUtils.generateToken(any(CustomUserDetails.class))).thenReturn(JWT_TOKEN);

        MessageForEmail data = MessageForEmail.builder()
                .recipient(registerRequest.getEmail())
                .subject("Test")
                .msgBody("Test")
                .build();
        producer.sendMessage(data);
        verify(producer).sendMessage(data);

        JwtResponse result = authService.register(registerRequest);
        assertEquals(data.getRecipient(), user.getEmail());
        assertEquals(registerRequest.getPassword(),registerRequest.getConfirmPassword());
        assertEquals(new JwtResponse(JWT_TOKEN),result);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User savedUser =captor.getValue();
        assertEquals(registerRequest.getEmail(), savedUser.getEmail());
    }

    @Test
    public void registration_shouldThrowExc(){
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.of(user));
        assertThrows(UsernameAlreadyException.class, ()->authService.register(registerRequest));
    }
    @Test
    public void registration_shouldThrowExc2(){
        registerRequest.setConfirmPassword("change password");
        assertThrows(PasswordIncorrectException.class, ()->authService.register(registerRequest));
    }

@Test
    public  void authenticateUser(){
    LoginRequest loginRequest = new LoginRequest(TEST_EMAIL, TEST_RAW_PASSWORD);
    when(jwtUtils.generateToken(any(CustomUserDetails.class))).thenReturn(JWT_TOKEN);
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

    when(authentication.getPrincipal()).thenReturn(customUserDetails);
    JwtResponse response = authService.authenticate(loginRequest);
    assertEquals(new JwtResponse(JWT_TOKEN),response);

}
}
