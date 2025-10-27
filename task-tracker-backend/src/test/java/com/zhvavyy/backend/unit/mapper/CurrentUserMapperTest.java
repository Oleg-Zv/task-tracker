package com.zhvavyy.backend.unit.mapper;

import com.zhvavyy.backend.dto.CurrentUserDto;
import com.zhvavyy.backend.mapper.CurrentUserMapper;
import com.zhvavyy.backend.model.User;
import com.zhvavyy.backend.model.enums.Role;
import com.zhvavyy.backend.unit.service.data.UserDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.zhvavyy.backend.unit.service.data.UserDataFactory.*;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
public class CurrentUserMapperTest {

    @InjectMocks
    private CurrentUserMapper mapper;
    private User user;

    @BeforeEach
    public void init(){
        user= UserDataFactory.createUser();
    }

    @Test
    public void mapTo(){
        CurrentUserDto dto = new CurrentUserDto(USER_ID,TEST_EMAIL, Role.USER);
        CurrentUserDto currentUserDto = mapper.mapTo(user);
        assertEquals(dto.id(), currentUserDto.id());
        assertEquals(dto.email(),user.getEmail());
        assertEquals(dto.role(),currentUserDto.role());
    }
}
