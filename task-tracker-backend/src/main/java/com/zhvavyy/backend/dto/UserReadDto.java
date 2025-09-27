package com.zhvavyy.backend.dto;

import com.zhvavyy.backend.model.enums.Role;

public record UserReadDto(Long id,
                          String email,
                          Role role,
                          String firstname,
                          String lastname) {
}
