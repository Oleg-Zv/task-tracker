package com.zhvavyy.backend.unit.web.jwt;

import com.zhvavyy.backend.model.User;
import com.zhvavyy.backend.web.jwt.JwtUtils;
import com.zhvavyy.backend.web.security.details.CustomUserDetails;
import com.zhvavyy.backend.unit.web.data.AuthDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("FieldCanBeLocal")
@ExtendWith(MockitoExtension.class)
public class JwtUtilsTest {

    @InjectMocks
    private JwtUtils jwtUtils;
    @Mock
    private JwtEncoder jwtEncoder;
    @Mock
    private JwtDecoder jwtDecoder;
    @Mock
    private Jwt jwt;

    private User user;
    private final String FAKE_TOKEN = "token.fake";


    @BeforeEach
    public void init(){
        user= AuthDataFactory.createUser();
    }

    @Test
    public void generateToken(){
        CustomUserDetails userDetails = new CustomUserDetails(user);
        Jwt mockedJwt = mock(Jwt.class);

       when(mockedJwt.getTokenValue()).thenReturn(FAKE_TOKEN);
       when(jwtEncoder.encode(any(JwtEncoderParameters.class)))
                .thenReturn(mockedJwt);

        String token = jwtUtils.generateToken(userDetails);

        assertNotNull(token);
        assertEquals(FAKE_TOKEN, token);
        verify(jwtEncoder).encode(any(JwtEncoderParameters.class));
    }

    @Test
    public void checkToken(){
        when(jwtDecoder.decode(FAKE_TOKEN)).thenReturn(jwt);
        boolean result = jwtUtils.checkToken(FAKE_TOKEN);
        assertTrue(result);
        verify(jwtDecoder).decode(FAKE_TOKEN);
    }
    @Test
    public void checkTokenFail(){
        when(jwtDecoder.decode(FAKE_TOKEN)).thenThrow(new JwtException("Invalid token"));
        boolean result = jwtUtils.checkToken(FAKE_TOKEN);
        assertFalse(result);
    }

    @Test
    public void getUsername() {
        Jwt jwtMock = mock(Jwt.class);
        when(jwtMock.getSubject()).thenReturn("subject");
        when(jwtDecoder.decode(FAKE_TOKEN)).thenReturn(jwtMock);

        String username = jwtUtils.getUsername(FAKE_TOKEN);
        assertEquals("subject", username);
        verify(jwtDecoder).decode(FAKE_TOKEN);
    }


}
