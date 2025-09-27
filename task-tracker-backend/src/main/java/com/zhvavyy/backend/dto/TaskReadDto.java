package com.zhvavyy.backend.dto;


import com.zhvavyy.backend.model.enums.Status;
import java.time.Instant;

public record TaskReadDto(Long id,
                          String title,
                          Status status,
                          String email,
                          Instant createdAt,
                          Instant doneAt) {
}
