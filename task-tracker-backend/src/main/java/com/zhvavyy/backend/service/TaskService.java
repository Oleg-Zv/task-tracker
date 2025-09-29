package com.zhvavyy.backend.service;

import com.zhvavyy.backend.dto.TaskCreateDto;
import com.zhvavyy.backend.dto.TaskReadDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface TaskService {
    void delete(Long id);
    Page<TaskReadDto>getAll(Pageable pageable);
    TaskReadDto getById(Long id);
    TaskReadDto add(TaskCreateDto taskDto);
    Page<TaskReadDto> findAllByUserId(Long id,Pageable pageable);
    Page<TaskReadDto> getAllPending(Pageable pageable);
    Page<TaskReadDto> getAllDone(Pageable pageable);
}
