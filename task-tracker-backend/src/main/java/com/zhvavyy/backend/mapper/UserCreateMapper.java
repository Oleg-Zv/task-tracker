package com.zhvavyy.backend.mapper;

import com.zhvavyy.backend.dto.UserUpdateDto;
import com.zhvavyy.backend.model.User;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserCreateMapper implements Mapper<UserUpdateDto, User> {

    PasswordEncoder passwordEncoder;


    @Override
    public User mapTo(UserUpdateDto object) {
        User user = new User();
        copy(object, user);
        return user;
    }

    @Override
    public User mapTo(UserUpdateDto fromObject, User toObject) {
        copy(fromObject, toObject);
        return toObject;
    }

    @Override
    public void copy(UserUpdateDto fromObject, User user){
        user.setFirstname(fromObject.getFirstname());
        user.setLastname(fromObject.getLastname());
        user.setEmail(fromObject.getEmail());
        user.setRole(fromObject.getRole());

         Optional.ofNullable(fromObject.getRawPassword())
                .filter(StringUtils::hasText)
                .map(passwordEncoder::encode)
                .ifPresent(user::setPassword);
    }
}
