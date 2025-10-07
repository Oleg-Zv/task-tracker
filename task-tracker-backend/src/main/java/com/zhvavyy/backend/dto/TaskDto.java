package com.zhvavyy.backend.dto;

import com.zhvavyy.backend.model.enums.Status;

public record TaskDto(Long id, String title, Status status) {}