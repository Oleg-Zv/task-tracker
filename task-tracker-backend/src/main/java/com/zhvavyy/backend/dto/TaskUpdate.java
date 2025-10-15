package com.zhvavyy.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskUpdate(
       @NotBlank(message = "Warn: title isEmpty!")
       @Size(max = 30)
        String title,
        @Size(max = 100)
        String description) {
}
