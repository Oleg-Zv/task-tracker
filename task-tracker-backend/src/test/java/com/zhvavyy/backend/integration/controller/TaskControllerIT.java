package com.zhvavyy.backend.integration.controller;

import com.zhvavyy.backend.dto.*;
import com.zhvavyy.backend.exception.TaskNotFoundException;
import com.zhvavyy.backend.integration.BaseIntegrationTest;
import com.zhvavyy.backend.model.User;
import com.zhvavyy.backend.model.enums.Role;
import com.zhvavyy.backend.model.enums.Status;
import com.zhvavyy.backend.service.TaskService;
import com.zhvavyy.backend.web.security.details.CustomUserDetails;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;


import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerIT extends BaseIntegrationTest {

    @MockitoBean
    private TaskService taskService;
    private CustomUserDetails customUserDetails;
    @Autowired
    private MockMvc mockMvc;

    private static final String BASE_URL = "/app/v1/tasks";
    private static final String TEST_TITLE = "Run 100 miles";
    private static final String TEST_EMAIL = "user@gmail.com";

    @BeforeEach
    public void init() {
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(1L);
        when(mockUser.getEmail()).thenReturn(TEST_EMAIL);
        when(mockUser.getRole()).thenReturn(Role.USER);
        customUserDetails = new CustomUserDetails(mockUser);
    }

    @Test
    @DisplayName("TaskController integration tests: when Status provided")
    @WithMockUser(username = "test@mail.com", roles = "USER")
    public void shouldReturnTasks_whenStatusProvided() throws Exception {

        Page<TaskReadDto> mockPage = new PageImpl<>(List.of(
                new TaskReadDto(1L, TEST_TITLE, Status.PENDING, TEST_EMAIL, Instant.now(), Instant.now())
        ));

        when(taskService.findAllByUserIdAndStatus(1L, Status.PENDING, PageRequest.of(0, 10)))
                .thenReturn(mockPage);

        mockMvc.perform(get(BASE_URL)
                        .param("status", "PENDING")
                        .param("page", "0")
                        .param("size", "10")
                        .with(user(customUserDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].title").value(TEST_TITLE))
                .andExpect(jsonPath("$.content[0].email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.content[0].status").value(Status.PENDING.name()));
    }

    @Test
    @DisplayName("TaskController integration tests: when Status null, return getAllTasks")
    @WithMockUser(username = "test@mail.com", roles = "USER")
    public void shouldReturnAllTasks_whenStatusIsNull() throws Exception {
        Page<TaskReadDto> mockPage = new PageImpl<>(List.of(
                new TaskReadDto(1L, TEST_TITLE, Status.PENDING, TEST_EMAIL, Instant.now(), Instant.now())
        ));

        when(taskService.findAllByUserId(1L, PageRequest.of(0, 10))).thenReturn(mockPage);

        mockMvc.perform(get(BASE_URL)
                        .param("page", "0")
                        .param("size", "10")
                        .with(user(customUserDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].title").value(TEST_TITLE));
    }

    @Test
    @DisplayName("TaskController integration tests: when getAllTasks result is empty")
    @WithMockUser(username = "test@mail.com", roles = "USER")
    void shouldReturnEmptyList_whenNoTasksExist() throws Exception {
        Page<TaskReadDto> emptyPage = new PageImpl<>(List.of());

        when(taskService.findAllByUserId(1L, PageRequest.of(0, 10)))
                .thenReturn(emptyPage);

        mockMvc.perform(get(BASE_URL)
                        .param("page", "0")
                        .param("size", "10")
                        .with(user(customUserDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    @DisplayName("TaskController integration tests: create task")
    void shouldCreateTask_whenInputValid() throws Exception {
        TaskReadDto taskReadDto = new TaskReadDto(1L, "test new", Status.PENDING, TEST_EMAIL, Instant.now(), Instant.now());
        String createCreateDto = """
                {
                    "title": "test new",
                    "description": "test new",
                    "status": "PENDING"
                }
                """;
        TaskCreateDto taskCreateDto = new TaskCreateDto("test new", "test new", Status.PENDING);

        when(taskService.add(taskCreateDto, customUserDetails)).thenReturn(taskReadDto);
        mockMvc.perform(post(BASE_URL)
                        .with(user(customUserDetails))
                        .content(createCreateDto)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("test new"))
                .andReturn();

    }

    @Test
    @DisplayName("TaskController integration tests: findAllByUserId exist")
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    public void shouldReturnTasksByUserId_whenTaskExist() throws Exception {
        TaskResponse taskResponse = new TaskResponse(List.of(new TaskDto(1L, TEST_EMAIL, TEST_TITLE, Status.PENDING)));

        when(taskService.findAllByUserId(1L)).thenReturn(taskResponse);

        mockMvc.perform(get(BASE_URL + "/{id}", "1").header("Authorization", "Bearer ")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tasks").isArray())
                .andExpect(jsonPath("$.tasks[0].email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.tasks[0].id").value(1L))
                .andExpect(jsonPath("$.tasks[0].title").value(TEST_TITLE))
                .andExpect(jsonPath("$.tasks[0].status").value(Status.PENDING.name()));

    }

    @Test
    @DisplayName("TaskController integration tests: findAllByUserId when no tasks")
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    public void shouldReturnTasksByUserIdEmptyList_whenNoTasks() throws Exception {
        when(taskService.findAllByUserId(0L)).thenReturn(new TaskResponse(List.of()));
        mockMvc.perform(get(BASE_URL + "/{id}", "0"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("TaskController integration tests: Done task by task_id")
    public void shouldMarkAsDone_whenTaskIdExist() throws Exception {
        TaskReadDto taskReadDto = new TaskReadDto(1L, "test new", Status.DONE, TEST_EMAIL, Instant.now(), Instant.now());

        when(taskService.markAsDone(1L, customUserDetails)).thenReturn(taskReadDto);

        mockMvc.perform(put(BASE_URL + "/{id}/done", "1")
                        .with(user(customUserDetails))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value(Status.DONE.name()));

    }

    @Test
    @DisplayName("TaskController integration tests: Done task, when task not found")
    public void shouldMarkAsDoneException_whenTaskIdNotFound() throws Exception {
        when(taskService.markAsDone(111L, customUserDetails)).thenThrow(TaskNotFoundException.class);

        mockMvc.perform(put(BASE_URL + "/{id}/done", "111")
                        .with(user(customUserDetails)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("TaskController integration tests: Pending task by task_id")
    public void shouldMarkAsPending_whenTaskIdExist() throws Exception {
        TaskReadDto taskReadDto = new TaskReadDto(2L, "test new", Status.PENDING, TEST_EMAIL, Instant.now(), Instant.now());

        when(taskService.markAsPending(2L, customUserDetails)).thenReturn(taskReadDto);

        mockMvc.perform(put(BASE_URL + "/{id}/pending", "2")
                        .with(user(customUserDetails))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.status").value(Status.PENDING.name()));
    }

    @Test
    @DisplayName("TaskController integration tests: Pending task, when task not found")
    public void shouldMarkAsPendingException_whenTaskIdNotFound() throws Exception {
        when(taskService.markAsPending(222L, customUserDetails)).thenThrow(TaskNotFoundException.class);

        mockMvc.perform(put(BASE_URL + "/{id}/pending", "222")
                        .with(user(customUserDetails)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("TaskController integration tests: delete task by id")
    public void shouldDeleteTask_whenTaskIdExist() throws Exception {
        doNothing().when(taskService).delete(1L);

        mockMvc.perform(delete(BASE_URL + "/{id}", "1")
                        .with(user(customUserDetails)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("TaskController integration tests: delete task, when Id invalid")
    public void shouldDeleteTask_whenIdInValid() throws Exception {

        doThrow(new TaskNotFoundException("task not found"))
                .when(taskService).delete(0L);

        mockMvc.perform(delete(BASE_URL + "/{id}", "0")
                        .with(user(customUserDetails)))
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("TaskController integration tests: change task by id")
    public void shouldChangeTask_whenTaskIdExist() throws Exception {
        TaskUpdate taskUpdate = new TaskUpdate("my life", "my world");
        TaskReadDto taskReadDto = new TaskReadDto(13L, "my life", Status.PENDING, TEST_EMAIL, Instant.now(), Instant.now());

        when(taskService.changeTask(13L, taskUpdate)).thenReturn(taskReadDto);

        mockMvc.perform(put(BASE_URL + "/{id}", "13")
                        .with(user(customUserDetails))
                        .content("""
                                {
                                    "title": "my life",
                                    "description": "my world"
                                }
                                """)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("my life"))
                .andExpect(jsonPath("$.id").value(13L));

    }

    @Test
    @DisplayName("TaskController integration tests: change task, should not found")
    public void shouldExceptionChangeTask_whenTaskIdInvalid() throws Exception {

        doThrow(new TaskNotFoundException("task not found"))
                .when(taskService).changeTask(eq(0L), any(TaskUpdate.class));
        mockMvc.perform(put(BASE_URL + "/{id}", "0")
                        .with(user(customUserDetails)).content("""
                                {
                                    "title": "rere",
                                    "description": "rere"
                                }
                                """)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}