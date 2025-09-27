package com.zhvavyy.backend.dto;

import com.zhvavyy.backend.model.enums.Role;


public record UserFilterDto(
        String email,
        Role role ) {
}
