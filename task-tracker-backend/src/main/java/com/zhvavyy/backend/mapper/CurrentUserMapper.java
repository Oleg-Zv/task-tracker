package com.zhvavyy.backend.mapper;

import com.zhvavyy.backend.dto.CurrentUserDto;
import com.zhvavyy.backend.model.User;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserMapper implements Mapper<User, CurrentUserDto> {


    @Override
    public CurrentUserDto mapTo(User object) {
        return new CurrentUserDto(
                object.getId(),
                object.getEmail(),
                object.getRole()
        );
    }
}
