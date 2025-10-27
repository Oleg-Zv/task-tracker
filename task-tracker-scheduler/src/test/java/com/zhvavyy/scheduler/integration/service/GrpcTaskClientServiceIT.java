package com.zhvavyy.scheduler.integration.service;

import com.zhvavyy.backend.grpc.TaskServiceScheduleProto;
import com.zhvavyy.scheduler.integration.BaseIntegrationTest;
import com.zhvavyy.scheduler.integration.service.mock.MockTaskService;
import com.zhvavyy.scheduler.service.GrpcTaskClientService;
import io.grpc.inprocess.InProcessServerBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = {
        "grpc.client.task-service.address=in-process:test-server",
        "grpc.client.task-service.negotiation-type=plaintext"
})
@DirtiesContext
public class GrpcTaskClientServiceIT extends BaseIntegrationTest {


    @Autowired
    private GrpcTaskClientService clientService;

    private static final String TEST_EMAIL = "grpc@gmail.com";
    private static final String TEST_TITLE = "my task";


    @BeforeEach
    public void setup() throws IOException {
        String serverName = "test-server";
        InProcessServerBuilder.forName(serverName)
                .addService(new MockTaskService())
                .build()
                .start();
    }

    @Test
    public void testGetResponseTask() {
        List<TaskServiceScheduleProto.TaskDto> tasks = clientService.getResponseTask(1L);

        assertEquals(1, tasks.size());
        assertEquals(TEST_TITLE, tasks.get(0).getTitle());
        assertEquals(TEST_EMAIL, tasks.get(0).getEmail());
    }
}