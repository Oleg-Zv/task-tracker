package com.zhvavyy.backend.mapper;

import com.zhvavyy.backend.dto.UserReadDto;
import com.zhvavyy.backend.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper implements Mapper<User, UserReadDto> {

    @Override
    public UserReadDto mapTo(User object) {
        return new UserReadDto(
                object.getId(),
                object.getEmail(),
                object.getRole(),
                object.getFirstname(),
                object.getLastname()
        );
    }
}
