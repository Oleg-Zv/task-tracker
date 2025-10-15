package com.zhvavyy.backend.web.dto;

import com.zhvavyy.backend.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RegisterRequest {

    @Email
    String email;

    @NotBlank
    @Size(min=6, max = 30)
    String password;

    @NotBlank
    @Size(min=6, max = 30)
    String confirmPassword;

    @Size(min = 2,max = 20)
    @NotBlank
    String firstname;

    @Size(min = 2,max = 20)
    @NotBlank
    String lastname;

    Role role;

}
