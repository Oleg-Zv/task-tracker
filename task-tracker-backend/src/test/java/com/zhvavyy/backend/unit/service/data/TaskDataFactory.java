package com.zhvavyy.backend.unit.service.data;

import com.zhvavyy.backend.dto.TaskReadDto;
import com.zhvavyy.backend.model.Task;
import com.zhvavyy.backend.model.User;
import com.zhvavyy.backend.model.enums.Role;
import com.zhvavyy.backend.model.enums.Status;

import java.time.Instant;

public final class TaskDataFactory {
    private TaskDataFactory(){}

    public static final Long TASK_ID = 1L;
    public static final String TEST_TITLE = "test title";
    public static final String TEST_DESC = "test description";
    public static final String TEST_UPDATE_TITLE = "new test title";
    public static final String TEST_UPDATE_DESC = "new test description";

    public static final Long USER_ID = 2L;
    public static final String TEST_EMAIL = "test@gmail.com";
    private static final String TEST_FIRSTNAME = "test user";
    private static final String TEST_LASTNAME = "test user";
    private static final Role TEST_ROLE = Role.USER;


    public static User createUser() {
        User user = new User();
        user.setId(USER_ID);
        user.setEmail(TEST_EMAIL);
        user.setFirstname(TEST_FIRSTNAME);
        user.setLastname(TEST_LASTNAME);
        user.setRole(TEST_ROLE);

        return user;
    }

    public static Task createTask() {
        Task task = new Task();
        task.setId(TASK_ID);
        task.setTitle(TEST_TITLE);
        task.setDescription(TEST_DESC);
        task.setStatus(Status.PENDING);
        task.setUser(createUser());

        return task;
    }

    public static TaskReadDto taskReadDtoHelper(Status status) {
        return new TaskReadDto(TASK_ID, TEST_TITLE,
                status, TEST_EMAIL,
                Instant.now(), Instant.now());
    }
}
