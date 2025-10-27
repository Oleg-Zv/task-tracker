package com.zhvavyy.backend.unit.mapper;

import com.zhvavyy.backend.dto.UserUpdateDto;
import com.zhvavyy.backend.mapper.UserCreateMapper;
import com.zhvavyy.backend.model.User;
import com.zhvavyy.backend.unit.service.data.UserDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserCreateMapperTest {

    @InjectMocks
    private UserCreateMapper userCreateMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    private UserUpdateDto userUpdateDto;
    private User user;

    @BeforeEach
    public void init(){
        userUpdateDto = UserDataFactory.getUserUpdateHelper();
        user= UserDataFactory.createUser();
    }

    @Test
    public void mapTo(){
        User user1 = userCreateMapper.mapTo(userUpdateDto);
        assertEquals(user1.getEmail(), userUpdateDto.getEmail());
        verify(passwordEncoder).encode(anyString());
    }
    @Test
    public void mapToTwoArgs(){
        User user1 = userCreateMapper.mapTo(userUpdateDto,user);
        assertEquals(user1.getEmail(), userUpdateDto.getEmail());
        verify(passwordEncoder).encode(anyString());
    }

    @Test
    public void copyTest_withPassword() {
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        userCreateMapper.copy(userUpdateDto, user);
       assertEquals("encoded", user.getPassword());
       verify(passwordEncoder).encode(userUpdateDto.getRawPassword());
    }
}
