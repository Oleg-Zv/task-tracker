package com.zhvavyy.backend.controller;


import com.zhvavyy.backend.dto.TaskReadDto;
import com.zhvavyy.backend.service.TaskServiceImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@Validated
@RequestMapping("/app/v1/")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TaskController {

    TaskServiceImpl taskService;

    @GetMapping("/tasks")
    public ResponseEntity<Page<TaskReadDto>> getAllTasks(Pageable pageable){
        Page<TaskReadDto> all = taskService.getAll(pageable);
        return ResponseEntity.ok(all);
    }
}
