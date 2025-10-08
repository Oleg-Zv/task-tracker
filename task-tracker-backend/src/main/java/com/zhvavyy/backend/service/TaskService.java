package com.zhvavyy.backend.service;

import com.zhvavyy.backend.dto.TaskCreateDto;
import com.zhvavyy.backend.dto.TaskReadDto;
import com.zhvavyy.backend.dto.TaskResponse;
import com.zhvavyy.backend.model.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface TaskService {
    void delete(Long id);
    Page<TaskReadDto>getAll(Pageable pageable);
    TaskReadDto getById(Long id);
    TaskReadDto add(TaskCreateDto taskDto);
    TaskResponse findAllByUserId(Long id);
    Page<TaskReadDto> findAllByStatus(Status status, Pageable pageable);
}
