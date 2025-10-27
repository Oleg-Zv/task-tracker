package com.zhvavyy.backend.unit.mapper;

import com.zhvavyy.backend.dto.UserReadDto;
import com.zhvavyy.backend.mapper.UserMapper;
import com.zhvavyy.backend.model.User;
import com.zhvavyy.backend.model.enums.Role;
import com.zhvavyy.backend.unit.service.data.UserDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserMapperTest {

    @InjectMocks
    private UserMapper userMapper;

    private UserReadDto userReadDto;
    private User user;

    @BeforeEach
    public void init() {
        userReadDto = UserDataFactory.getUserReadHelper(Role.USER);
        user = UserDataFactory.createUser();
    }

    @Test
    public void mapToDto(){
        UserReadDto result = userMapper.mapTo(user);
        assertEquals(userReadDto,result);
        assertEquals(userReadDto.email(),result.email());
    }
}
