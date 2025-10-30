package com.zhvavyy.backend.integration.grpc;

import com.zhvavyy.backend.grpc.TaskServiceScheduleGrpc;
import com.zhvavyy.backend.grpc.TaskServiceScheduleProto;
import com.zhvavyy.backend.integration.BaseIntegrationTest;
import com.zhvavyy.backend.model.Task;
import com.zhvavyy.backend.model.User;
import com.zhvavyy.backend.model.enums.Role;
import com.zhvavyy.backend.model.enums.Status;
import com.zhvavyy.backend.repository.TaskRepository;
import com.zhvavyy.backend.repository.UserRepository;
import io.grpc.Metadata;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.stub.MetadataUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "grpc.server.port=-1",
        "grpc.server.in-process-name=test-server"
})
@DirtiesContext
public class TaskServiceScheduleImplIT extends BaseIntegrationTest {


    private TaskServiceScheduleGrpc.TaskServiceScheduleBlockingStub stub;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;

    private static final String TEST_EMAIL = "grpc@gmail.com";
    private static final String TEST_FNAME = "grpc name";
    private static final String TEST_LNAME = "tests";
    private static final String TEST_PASS = "tests23";
    private static final String TEST_TITLE = "my task";
    private static final String TEST_DESC = "my description";

    private User existingUser;

    @BeforeEach
    public void init() {
        String serverName = "test-server";
        stub = TaskServiceScheduleGrpc.newBlockingStub(
                (InProcessChannelBuilder.forName(serverName)
                        .directExecutor()
                        .build())
        );

        existingUser = userRepository.save(User.builder()
                .email(TEST_EMAIL)
                .role(Role.USER)
                .firstname(TEST_FNAME)
                .lastname(TEST_LNAME)
                .password(TEST_PASS)
                .build());
    }

    @Test
    public void findAllByUserId_whenUserHasNoTasks() {
        TaskServiceScheduleProto.SchedulerTasksRequest request =
                TaskServiceScheduleProto.SchedulerTasksRequest.newBuilder()
                        .setId(existingUser.getId())
                        .build();

        TaskServiceScheduleProto.TaskResponse response = stub.findAllByUserId(request);

        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(0, response.getTasksCount())
        );
    }

    @Test
    public void findAllByUserId_whenTasksExist() {
        Instant now = Instant.now();
        taskRepository.save(Task.builder()
                .user(existingUser)
                .title(TEST_TITLE)
                .description(TEST_DESC)
                .status(Status.DONE)
                .createdAt(now)
                .doneAt(now)
                .build());

        // Создаем заголовки с user-id
        Metadata headers = new Metadata();
        headers.put(Metadata.Key.of("user-id", Metadata.ASCII_STRING_MARSHALLER),
                String.valueOf(existingUser.getId()));

        TaskServiceScheduleProto.SchedulerTasksRequest request =
                TaskServiceScheduleProto.SchedulerTasksRequest.newBuilder()
                        .setId(existingUser.getId()) // это поле сейчас не используется сервисом!
                        .build();

        // Вызываем с заголовками
        TaskServiceScheduleProto.TaskResponse response = stub
                .withInterceptors(MetadataUtils.newAttachHeadersInterceptor(headers))
                .findAllByUserId(request);

        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(1, response.getTasksCount()),
                () -> assertEquals(TEST_TITLE, response.getTasks(0).getTitle())
        );
    }
    @AfterEach
    public void cleanUp() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
    }
}
