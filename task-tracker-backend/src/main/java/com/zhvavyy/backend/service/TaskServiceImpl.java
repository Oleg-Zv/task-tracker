package com.zhvavyy.backend.service;

import com.zhvavyy.backend.dto.*;
import com.zhvavyy.backend.exception.TaskNotFoundException;
import com.zhvavyy.backend.mapper.TaskCreateMapper;
import com.zhvavyy.backend.mapper.TaskMapper;
import com.zhvavyy.backend.model.Task;
import com.zhvavyy.backend.model.enums.Status;
import com.zhvavyy.backend.repository.TaskRepository;
import com.zhvavyy.backend.web.security.details.CustomUserDetails;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.access.AccessDeniedException;
import java.time.Instant;
import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TaskServiceImpl implements TaskService {

    TaskRepository taskRepository;
    TaskCreateMapper taskCreateMapper;
    TaskMapper taskMapper;


    @Transactional
    @Override
    public void delete(Long id) {
        taskRepository.findById(id)
                .ifPresentOrElse(taskRepository::delete,
                        () -> {throw new TaskNotFoundException("task not found with id: " + id);});
    }

    @Override
    public Page<TaskReadDto> getAll(Pageable pageable) {
      return taskRepository.findAll(pageable)
              .map(taskMapper::mapTo);
    }

    @Override
    public TaskReadDto getById(Long id) {
        return getByIdOrThrow(id);
    }

    @Override
    @Transactional
    public TaskReadDto add(TaskCreateDto taskDto, CustomUserDetails userDetails) {
        Task task = taskCreateMapper.mapTo(taskDto);
        task.setUser(userDetails.getUser());
        return taskMapper
                .mapTo(taskRepository.save(task));
    }

    @Override
    public TaskResponse findAllByUserId(Long userId) {
        List<TaskDto> tasks = taskRepository.findAllByUserId(userId)
                .stream().map(task -> new TaskDto(task.getId(), task.getUser().getEmail(), task.getTitle(), task.getStatus()))
                .toList();
        return new TaskResponse(tasks);
    }

    @Override
    @Transactional
    public TaskReadDto markAsDone(Long id, CustomUserDetails userDetails) {
        return changeStatus(id, userDetails, Status.DONE);
    }

    @Override
    @Transactional
    public TaskReadDto markAsPending(Long id, CustomUserDetails userDetails) {
        return changeStatus(id, userDetails, Status.PENDING);
    }

    @Override
    @Transactional
    public TaskReadDto changeTask(Long id, TaskUpdate taskUpdate) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task with id:" + id + " not found"));

        if (taskUpdate.title() != null) task.setTitle(taskUpdate.title());
        if (taskUpdate.description() != null) task.setDescription(taskUpdate.description());

        return taskMapper.mapTo(taskRepository.save(task));
    }

    @Override
    public Page<TaskReadDto> findAllByStatus(Status status, Pageable pageable) {
        return taskRepository.findAllByStatus(status, pageable)
                .map(taskMapper::mapTo);
    }

    private TaskReadDto changeStatus(Long id, CustomUserDetails userDetails, Status status) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));

        if (!task.getUser().getId().equals(userDetails.getUser().getId())) {
            throw new AccessDeniedException("No access right");
        }
        task.setStatus(status);
        if (status == Status.PENDING) task.setDoneAt(null);
        else task.setDoneAt(Instant.now());

        taskRepository.save(task);

        return taskMapper.mapTo(task);
    }

    private TaskReadDto getByIdOrThrow(Long id) {
        return taskRepository.findById(id)
                .map(taskMapper::mapTo)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id " + id));
    }
}
