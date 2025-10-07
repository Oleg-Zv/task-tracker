package com.zhvavyy.backend.service;

import com.zhvavyy.backend.dto.TaskCreateDto;
import com.zhvavyy.backend.dto.TaskReadDto;
import com.zhvavyy.backend.dto.TaskResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface TaskService {
    void delete(Long id);
    Page<TaskReadDto>getAll(Pageable pageable);
    TaskReadDto getById(Long id);
    TaskReadDto add(TaskCreateDto taskDto);
    TaskResponse findAllByUserId(Long id);
    Page<TaskReadDto> getAllPending(Pageable pageable);
    Page<TaskReadDto> getAllDone(Pageable pageable);
}
