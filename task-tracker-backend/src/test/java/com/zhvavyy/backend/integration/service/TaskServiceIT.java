package com.zhvavyy.backend.integration.service;

import com.zhvavyy.backend.dto.*;
import com.zhvavyy.backend.exception.TaskNotFoundException;
import com.zhvavyy.backend.integration.BaseIntegrationTest;
import com.zhvavyy.backend.model.Task;
import com.zhvavyy.backend.model.User;
import com.zhvavyy.backend.model.enums.Role;
import com.zhvavyy.backend.model.enums.Status;
import com.zhvavyy.backend.repository.TaskRepository;
import com.zhvavyy.backend.repository.UserRepository;
import com.zhvavyy.backend.service.TaskService;
import com.zhvavyy.backend.web.security.details.CustomUserDetails;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@AutoConfigureMockMvc
@DisplayName("Integration test for TaskService")
public class TaskServiceIT extends BaseIntegrationTest {

    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;
    private CustomUserDetails userDetails;

    private static final String TEST_EMAIL = "test@gmail.com";
    private static final String TEST_TITLE = "my task";
    private static final String TEST_DESC = "my description";
    private static final String UPDATE_TITLE = "update title";
    private static final String UPDATE_DESC = "update desc";
    private static final String CREATE_TITLE = "new title";
    private static final String CREATE_DESC = "new desc";
    private static final Long ID_INVALID = -1L;

    private Task savedTask;
    private User existingUser;
    private TaskReadDto expectedTaskReadDto;
    private TaskCreateDto taskCreateDto;
    private List<TaskDto> expectedTasks;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
        existingUser = userRepository.save(User.builder()
                .email(TEST_EMAIL)
                .role(Role.USER)
                .firstname("name")
                .lastname("heros")
                .password("temp")
                .build());
        Instant now = Instant.now();
        Task task = new Task(null, TEST_TITLE, TEST_DESC, existingUser, Status.DONE, now, now);
        savedTask = taskRepository.save(task);

        expectedTaskReadDto = new TaskReadDto(null, TEST_TITLE, Status.DONE, TEST_EMAIL, now, now);
        taskCreateDto = new TaskCreateDto(CREATE_TITLE, CREATE_DESC, Status.PENDING);
        userDetails = new CustomUserDetails(existingUser);
        expectedTasks = List.of(new TaskDto(null, TEST_EMAIL, TEST_TITLE, Status.DONE));
    }

    @Test
    @DisplayName("delete(): Should delete existing task by ID")
    public void deleteById_shouldDeleteExistingTask() {
        Optional<Task> existingTask = taskRepository.findById(savedTask.getId());
        assertTrue(existingTask.isPresent());
        assertEquals(TEST_EMAIL, savedTask.getUser().getEmail());

        taskService.delete(savedTask.getId());
        Optional<Task> deletedTask = taskRepository.findById(savedTask.getId());
        assertTrue(deletedTask.isEmpty());
    }

    @Test
    @DisplayName("delete(): Should throw exception when deleting non-existing task")
    public void deleteById_shouldThrowException_whenNotFound() {
        assertThrows(TaskNotFoundException.class, () -> taskService.delete(ID_INVALID));
    }

    @Test
    @DisplayName("getAllTasks(): Should return all existing tasks")
    public void getAllTasks() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<TaskReadDto> all = taskService.getAll(pageable);

        TaskReadDto result = all.getContent().stream()
                .filter(t -> t.title().equals(TEST_TITLE) && t.email().equals(TEST_EMAIL))
                .findFirst()
                .orElseThrow();

        assertAll(
                () -> assertFalse(all.isEmpty()),
                () -> assertEquals(expectedTaskReadDto.email(), all.getContent().get(0).email()),
                () -> assertEquals(TEST_TITLE, result.title()),
                () -> assertEquals(TEST_EMAIL, result.email())
        );
    }

    @Test
    @DisplayName("getAllTasks(): Should return empty page when no tasks exist")
    public void getAllTasks_whenNoTasks() {
        taskRepository.deleteAll();
        PageRequest pageable = PageRequest.of(0, 10);
        Page<TaskReadDto> shouldNoTasks = taskService.getAll(pageable);
        assertNotNull(shouldNoTasks);
        assertTrue(shouldNoTasks.isEmpty());
    }

    @Test
    @DisplayName("getTaskById(): Should return correct task by ID")
    public void getTaskById_shouldReturnCorrectDto() {
        TaskReadDto taskReadDtoActual = taskService.getById(savedTask.getId());
        assertAll(
                () -> assertNotNull(taskReadDtoActual),
                () -> assertEquals(expectedTaskReadDto.email(), taskReadDtoActual.email()),
                () -> assertEquals(expectedTaskReadDto.title(), taskReadDtoActual.title())
        );
    }

    @Test
    @DisplayName("getTaskById(): Should throw exception when task not found by ID")
    public void getTaskById_shouldThrowException_whenNotFound() {
        Optional<Task> taskNoExist = taskRepository.findById(ID_INVALID);
        assertTrue(taskNoExist.isEmpty());
        assertThrows(TaskNotFoundException.class, () -> taskService.getById(ID_INVALID));
    }

    @Test
    @DisplayName("createTask(): Should create a new task successfully")
    public void createTask() {
        TaskReadDto newTask = taskService.add(taskCreateDto, userDetails);
        assertAll(
                () -> assertNotNull(newTask),
                () -> assertEquals(CREATE_TITLE, newTask.title()),
                () -> assertNotEquals(expectedTaskReadDto.title(), newTask.title())
        );
        TaskReadDto result = taskService.getById(newTask.id());
        assertEquals(newTask.id(), result.id());
    }

    @Test
    @DisplayName("findAllTasksByUserId(): Should return all tasks for a specific user")
    public void findAllTasksByUserId_shouldReturnCorrectResponse() {
        TaskResponse allByUserId = taskService.findAllByUserId(existingUser.getId());
        assertAll(
                () -> assertNotNull(allByUserId),
                () -> assertEquals(expectedTasks.get(0).title(), allByUserId.tasks().get(0).title()),
                () -> assertEquals(existingUser.getEmail(), allByUserId.tasks().get(0).email()),
                () -> assertEquals(expectedTasks.get(0).status(), allByUserId.tasks().get(0).status())
        );
    }

    @Test
    @DisplayName("shouldMarkAsDone(): Should mark task as DONE when it exists")
    public void shouldMarkAsDone_whenTaskIdExist() {
        savedTask.setStatus(Status.PENDING);
        TaskReadDto result = taskService.markAsDone(savedTask.getId(), userDetails);
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(Status.DONE, result.status())
        );
    }

    @Test
    @DisplayName("shouldMarkAsDone(): Should throw exception when marking as DONE non-existing task")
    public void shouldMarkAsDoneException_whenTaskIdNotFound() {
        assertThrows(TaskNotFoundException.class, () -> taskService.markAsDone(ID_INVALID, userDetails));
    }

    @Test
    @DisplayName("shouldMarkAsPending(): Should mark task as PENDING when it exists")
    public void shouldMarkAsPending_whenTaskIdExist() {
        savedTask.setStatus(Status.DONE);
        taskRepository.save(savedTask);
        assertEquals(Status.DONE, savedTask.getStatus());

        TaskReadDto result = taskService.markAsPending(savedTask.getId(), userDetails);
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(Status.PENDING, result.status())
        );
    }

    @Test
    @DisplayName("shouldMarkAsPending(): Should throw exception when marking as PENDING non-existing task")
    public void shouldMarkAsPendingException_whenTaskIdNotFound() {
        assertThrows(TaskNotFoundException.class, () -> taskService.markAsPending(ID_INVALID, userDetails));
    }

    @Test
    @DisplayName("shouldChangeTask(): Should change existing task successfully")
    public void shouldChangeTask_whenTaskIdExist() {
        savedTask.setTitle(UPDATE_TITLE);
        savedTask.setDescription(UPDATE_DESC);

        TaskUpdate taskUpdate = new TaskUpdate(UPDATE_TITLE, UPDATE_DESC);
        TaskReadDto result = taskService.changeTask(savedTask.getId(), taskUpdate);
        Task update = taskRepository.findById(savedTask.getId()).orElseThrow();

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(UPDATE_TITLE, update.getTitle()),
                () -> assertEquals(UPDATE_DESC, update.getDescription())
        );
    }

    @Test
    @DisplayName("shouldChangeTask(): Should throw exception when changing non-existing task")
    public void shouldExceptionChangeTask_whenTaskIdInvalid() {
        TaskUpdate taskUpdate = new TaskUpdate(UPDATE_TITLE, UPDATE_DESC);
        assertThrows(TaskNotFoundException.class, () -> taskService.changeTask(ID_INVALID, taskUpdate));
    }

    @AfterEach
    void clearUp() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
    }
}
