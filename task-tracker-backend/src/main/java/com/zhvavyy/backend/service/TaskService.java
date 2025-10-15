package com.zhvavyy.backend.service;

import com.zhvavyy.backend.dto.TaskCreateDto;
import com.zhvavyy.backend.dto.TaskReadDto;
import com.zhvavyy.backend.dto.TaskResponse;
import com.zhvavyy.backend.model.enums.Status;
import com.zhvavyy.backend.web.security.details.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface TaskService {
    void delete(Long id);
    Page<TaskReadDto>getAll(Pageable pageable);
    TaskReadDto getById(Long id);
    TaskReadDto add(TaskCreateDto taskDto, CustomUserDetails userDetails);
    TaskResponse findAllByUserId(Long id);
    Page<TaskReadDto> findAllByStatus(Status status, Pageable pageable);
    TaskReadDto markAsDone(Long id,CustomUserDetails userDetails);
    TaskReadDto markAsPending(Long id,CustomUserDetails userDetails);
}
