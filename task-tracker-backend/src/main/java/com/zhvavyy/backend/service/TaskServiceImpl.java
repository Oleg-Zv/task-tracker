package com.zhvavyy.backend.service;

import com.zhvavyy.backend.dto.TaskCreateDto;
import com.zhvavyy.backend.dto.TaskReadDto;
import com.zhvavyy.backend.exception.TaskNotFoundException;
import com.zhvavyy.backend.exception.UnauthorizedException;
import com.zhvavyy.backend.mapper.TaskCreateMapper;
import com.zhvavyy.backend.mapper.TaskMapper;
import com.zhvavyy.backend.model.enums.Status;
import com.zhvavyy.backend.repository.TaskRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class TaskServiceImpl implements TaskService {

    TaskRepository taskRepository;
    TaskCreateMapper taskCreateMapper;
    TaskMapper taskMapper;


    @Transactional
    @Override
    public void delete(Long id) {
       taskRepository.findById(id)
              .ifPresentOrElse(taskRepository::delete,
             ()->{ throw new TaskNotFoundException("task not found with id: "+id);});
    }

    @Override
    public Page<TaskReadDto> getAll(Pageable pageable) {
        return taskRepository.findAll(pageable)
                .map(taskMapper::mapTo)
                .map(taskReadDto -> {
                    throw new UnauthorizedException("unauthorized user");
                });
    }

    @Override
    public TaskReadDto getById(Long id) {
        return getByIdOrThrow(id);
    }

    @Override
    @Transactional
    public TaskReadDto add(TaskCreateDto taskDto) {
         return taskMapper
                 .mapTo(taskRepository
                 .save(taskCreateMapper.mapTo(taskDto)));
    }

    @Override
    public Page<TaskReadDto> findAllByUserId(Long userId, Pageable pageable) {
        return taskRepository.findAllByUserId(userId, pageable)
                             .map(taskMapper::mapTo);
    }

    @Override
    public Page<TaskReadDto> getAllPending(Pageable pageable) {
       return filterByStatus(Status.PENDING,pageable);
    }

    @Override
    public Page<TaskReadDto> getAllDone(Pageable pageable) {
      return filterByStatus(Status.DONE,pageable);
    }

    @Override
    public Page<TaskReadDto> getAllActive(Pageable pageable) {
        return filterByStatus(Status.ACTIVE,pageable);
    }

    private Page<TaskReadDto> filterByStatus(Status status, Pageable pageable) {

        return taskRepository.findAllByStatus(status, pageable)
                .map(taskMapper::mapTo);

    }

    private TaskReadDto getByIdOrThrow(Long id){
        return taskRepository.findById(id)
                .map(taskMapper::mapTo)
                .orElseThrow(()->new TaskNotFoundException("Task not found with id " +id));
    }
}
