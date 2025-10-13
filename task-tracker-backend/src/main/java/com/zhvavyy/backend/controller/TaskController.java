package com.zhvavyy.backend.controller;


import com.zhvavyy.backend.dto.TaskCreateDto;
import com.zhvavyy.backend.dto.TaskReadDto;
import com.zhvavyy.backend.dto.TaskResponse;
import com.zhvavyy.backend.model.enums.Status;
import com.zhvavyy.backend.service.TaskService;
import com.zhvavyy.backend.web.security.details.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@Validated
@RequestMapping("/app/v1/tasks")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TaskController {

    TaskService taskService;

    @GetMapping
    public ResponseEntity<Page<TaskReadDto>> getTasks(@RequestParam(required = false) Status status,
                                                              Pageable pageable){
        if(status!=null) {
            return ResponseEntity.ok(taskService.findAllByStatus(status, pageable));
        }
        return ResponseEntity.ok(taskService.getAll(pageable));
    }

    @PostMapping
    public ResponseEntity<TaskReadDto> add(@RequestBody @Valid TaskCreateDto taskCreateDto,
                                           Authentication authentication) {
        CustomUserDetails userDetails =(CustomUserDetails)authentication.getPrincipal();
        TaskReadDto task = taskService.add(taskCreateDto,userDetails);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> findById(@PathVariable Long id){
        return ResponseEntity.ok(taskService.findAllByUserId(id));
    }
}
