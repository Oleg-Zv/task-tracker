package com.zhvavyy.backend.dto;

import com.zhvavyy.backend.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Value;
import lombok.experimental.FieldNameConstants;

@Value
@FieldNameConstants
public class UserUpdateDto {

    @Size(min = 2, max = 20)
    @NotBlank
    String firstname;
    @NotBlank
    @Size(min = 2, max = 20)
    String lastname;

    @NotBlank
    String rawPassword;

    @Email
    String email;
    Role role;
}
