package com.zhvavyy.unit.service;


import com.zhvavyy.backend.dto.*;
import com.zhvavyy.backend.exception.TaskNotFoundException;
import com.zhvavyy.backend.mapper.TaskCreateMapper;
import com.zhvavyy.backend.mapper.TaskMapper;
import com.zhvavyy.backend.model.Task;
import com.zhvavyy.backend.model.User;
import com.zhvavyy.backend.model.enums.Status;
import com.zhvavyy.backend.repository.TaskRepository;
import com.zhvavyy.backend.service.TaskServiceImpl;
import com.zhvavyy.backend.web.security.details.CustomUserDetails;
import com.zhvavyy.unit.service.data.TaskDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static com.zhvavyy.unit.service.data.TaskDataFactory.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("FieldCanBeLocal")
@ExtendWith(MockitoExtension.class)
public class TaskServiceImplTest {

    @Mock
    private TaskRepository repository;
    @InjectMocks
    private TaskServiceImpl taskService;
    @Mock
    private TaskMapper taskMapper;
    @Mock
    private TaskCreateMapper createMapper;
    @Mock
    private CustomUserDetails userDetails;

    private Task task;
    private User user;
    private TaskReadDto taskReadDone;
    private TaskReadDto taskReadPending;


    @BeforeEach
    public void init(){
         task = createTask();
         user = createUser();
         taskReadDone= taskReadDtoHelper(Status.DONE);
         taskReadPending= taskReadDtoHelper(Status.PENDING);
    }

    @Test
    public void deleteById() {
        when(repository.findById(TASK_ID)).thenReturn(Optional.of(task));

        taskService.delete(task.getId());
        verify(repository).delete(task);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void delete_NotFound_Id() {
        when(repository.findById(TASK_ID)).thenReturn(Optional.empty());
        assertThrows(TaskNotFoundException.class, () -> taskService.delete(TASK_ID));
    }

    @Test
    public void getAllTasks() {

        PageRequest pageable = PageRequest.of(0, 10);
        Page<Task> page = new PageImpl<>(List.of(task));

        when(taskMapper.mapTo(task)).thenReturn(taskReadPending);
        when(repository.findAll(pageable)).thenReturn(page);

        Page<TaskReadDto> result = taskService.getAll(pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(taskReadPending.title(), result.getContent().get(0).title());
        assertEquals(taskReadPending.status().name(), result.getContent().get(0).status().name());
    }

    @Test
    public void getAllTasks_Fail_IsEmpty() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(repository.findAll(pageable)).thenReturn(Page.empty(pageable));
        Page<TaskReadDto> all = taskService.getAll(pageable);
        assertTrue(all.getContent().isEmpty());
    }

    @Test
    public void getTaskById() {

        when(repository.findById(TASK_ID)).thenReturn(Optional.of(task));
        when(taskMapper.mapTo(task)).thenReturn(taskReadPending);

        TaskReadDto result = taskService.getById(TASK_ID);
        assertNotNull(result);

        verifyNoMoreInteractions(repository);
        assertEquals(task.getId(), result.id());
    }

    @Test
    public void getByTaskId_NotFound() {
        when(repository.findById(TASK_ID)).thenReturn(Optional.empty());
        assertThrows(TaskNotFoundException.class, () -> taskService.getById(TASK_ID));
    }

    @Test
    public void addTask() {
        TaskCreateDto createDto = new TaskCreateDto(TEST_TITLE, TEST_DESC, Status.DONE);

        when(createMapper.mapTo(createDto)).thenReturn(task);
        when(userDetails.getUser()).thenReturn(user);
        when(taskMapper.mapTo(task)).thenReturn(taskReadPending);
        when((repository.save(task))).thenReturn(task);

        TaskReadDto result = taskService.add(createDto, userDetails);
        assertNotNull(result);
        verify(repository).save(task);

        assertEquals(task.getTitle(), result.title());
        assertEquals(task.getStatus().name(), result.status().name());
    }

    @Test
    public void findAllTasks_ByUserId() {
        TaskDto taskDto = new TaskDto(TASK_ID, TEST_EMAIL, TEST_TITLE, Status.DONE);

        when(repository.findAllByUserId(user.getId())).thenReturn(List.of(task));

        TaskResponse allByUserId = taskService.findAllByUserId(user.getId());
        assertNotNull(allByUserId);
        verify(repository).findAllByUserId(user.getId());
        assertEquals(taskDto.email(), allByUserId.tasks().get(0).email());
        assertEquals(1, allByUserId.tasks().size());
    }

    @Test
    public void findAllTasks_ByUserId_IsNotFoundId() {
        when(repository.findAllByUserId(TaskDataFactory.USER_ID)).thenReturn(List.of());
        TaskResponse allByUserId = taskService.findAllByUserId(USER_ID);
        assertTrue(allByUserId.tasks().isEmpty());
        verify(repository).findAllByUserId(USER_ID);
    }

    @ParameterizedTest
    @EnumSource(Status.class)
    public void shouldChangeStatusCorrectly(Status status) {

        if (status == Status.DONE) {
            task.setStatus(Status.DONE);
            when(repository.findById(TASK_ID)).thenReturn(Optional.of(task));
            when(userDetails.getUser()).thenReturn(user);
            when(taskMapper.mapTo(task)).thenReturn(taskReadDone);
            when(repository.save(task)).thenReturn(task);

            TaskReadDto result = taskService.markAsDone(TASK_ID, userDetails);
            assertEquals(Status.DONE, result.status());
        } else {
            when(repository.findById(TASK_ID)).thenReturn(Optional.of(task));
            when(userDetails.getUser()).thenReturn(user);
            when(taskMapper.mapTo(task)).thenReturn(taskReadPending);
            when(repository.save(task)).thenReturn(task);

            TaskReadDto result = taskService.markAsPending(TASK_ID, userDetails);
            assertEquals(Status.PENDING, result.status());
        }

        verify(repository).save(task);
    }

    @Test
    public void changeStatus_NotFoundId() {
        when(repository.findById(TASK_ID)).thenReturn(Optional.empty());
        assertThrows(TaskNotFoundException.class, () -> taskService.markAsDone(TASK_ID, userDetails));

    }

    @Test
    public void changeStatus_UserInvalidId() {
        Task task = createTask();

        when(repository.findById(TASK_ID)).thenReturn(Optional.of(task));
        when(userDetails.getUser()).thenReturn(new User());

        assertThrows(AccessDeniedException.class, () -> taskService.markAsDone(task.getId(), userDetails));

    }

    @Test
    public void changeTask() {
        TaskUpdate taskUpdate = new TaskUpdate(TEST_UPDATE_TITLE, TEST_UPDATE_DESC);

        when(repository.findById(TASK_ID)).thenReturn(Optional.of(task));
        when(repository.save(task)).thenReturn(task);
        when(taskMapper.mapTo(task)).thenReturn(taskReadPending);

        TaskReadDto result = taskService.changeTask(task.getId(), taskUpdate);
        assertEquals(result.title(), taskReadPending.title());
        assertEquals(TEST_UPDATE_TITLE, task.getTitle());
        assertEquals(TEST_UPDATE_DESC, task.getDescription());
        verify(repository).save(task);
    }

    @ParameterizedTest
    @EnumSource(Status.class)
    public void shouldFindAllTasksByStatus(Status status) {
        task.setStatus(status);
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Task> page = new PageImpl<>(List.of(task));
        TaskReadDto taskReadDto = taskReadDtoHelper(status);

        when(repository.findAllByStatus(status, pageable)).thenReturn(page);
        when(taskMapper.mapTo(task)).thenReturn(taskReadDto);

        Page<TaskReadDto> result = taskService.findAllByStatus(status, pageable);
        assertEquals(status, result.getContent().get(0).status());
    }

}
