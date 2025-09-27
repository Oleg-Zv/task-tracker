package com.zhvavyy.backend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import static org.springframework.util.StringUtils.*;

@Component
public class UserValidator implements ConstraintValidator<UserInfo, UserInfoValidation> {


    @Override
    public boolean isValid(UserInfoValidation value, ConstraintValidatorContext context) {
        return hasText(value.getFirstname()) || hasText(value.getLastname());
    }
}
