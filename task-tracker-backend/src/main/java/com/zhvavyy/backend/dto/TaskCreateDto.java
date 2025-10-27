package com.zhvavyy.backend.dto;

import com.zhvavyy.backend.model.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;


@Value
public class TaskCreateDto {

    @NotBlank
    @Size(min=2, max=30)
    String title;

    @Size(max = 100)
    @NotBlank
    String description;

    @NotNull
    Status status;


}
