package com.zhvavyy.backend.dto;

import com.zhvavyy.backend.model.enums.Role;

public record CurrentUserDto(
        Long id,
        String email,
        Role role
) {
}
