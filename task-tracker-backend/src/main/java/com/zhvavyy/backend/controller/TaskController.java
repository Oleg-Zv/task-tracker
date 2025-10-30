package com.zhvavyy.backend.controller;


import com.zhvavyy.backend.dto.TaskCreateDto;
import com.zhvavyy.backend.dto.TaskReadDto;
import com.zhvavyy.backend.dto.TaskResponse;
import com.zhvavyy.backend.dto.TaskUpdate;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
                                                      Pageable pageable,
                                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();

        if(status != null) {
            return ResponseEntity.ok(taskService.findAllByUserIdAndStatus(userId, status, pageable));
        }
        return ResponseEntity.ok(taskService.findAllByUserId(userId, pageable));
    }

    @PostMapping
    public ResponseEntity<TaskReadDto> add(@RequestBody @Valid TaskCreateDto taskCreateDto,
                                           Authentication authentication) {
        CustomUserDetails userDetails =(CustomUserDetails)authentication.getPrincipal();
        TaskReadDto task = taskService.add(taskCreateDto,userDetails);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.findAllByUserId(id));
    }

    @PutMapping("/{id}/done")
    public ResponseEntity<TaskReadDto> markAsDone(@PathVariable Long id, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        TaskReadDto updated = taskService.markAsDone(id, userDetails);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/pending")
    public ResponseEntity<TaskReadDto> markTaskPending(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(taskService.markAsPending(id, userDetails));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskReadDto> changeTask(@PathVariable Long id,
                                                  @RequestBody @Valid TaskUpdate update) {
        return ResponseEntity.ok(taskService.changeTask(id, update));
    }

}
