package com.zhvavyy.backend.dto;

import com.zhvavyy.backend.model.enums.Status;

import java.util.List;

public record TaskResponse(List<TaskDto>tasks) {
}
