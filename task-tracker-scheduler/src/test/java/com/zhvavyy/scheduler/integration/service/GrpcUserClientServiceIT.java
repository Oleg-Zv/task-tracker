package com.zhvavyy.scheduler.integration.service;

import com.zhvavyy.backend.grpc.UserServiceScheduleProto;
import com.zhvavyy.scheduler.integration.enums.Role;
import com.zhvavyy.scheduler.integration.service.mock.MockUserService;
import com.zhvavyy.scheduler.service.GrpcUserClientService;
import io.grpc.inprocess.InProcessServerBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = {
        "grpc.client.user-service.address=in-process:test-server2",
        "grpc.client.user-service.negotiation-type=plaintext"
})
@DirtiesContext
public class GrpcUserClientServiceIT {

    @Autowired
    private GrpcUserClientService clientService;

    private static final String TEST_EMAIL = "grpc@gmail.com";
    private static final String TEST_FNAME = "Poly";
    private static final String TEST_LNAME = "Sorry";

    @BeforeEach
    public void setup() throws IOException {
        String serverName = "test-server2";
        InProcessServerBuilder.forName(serverName)
                .addService(new MockUserService())
                .build()
                .start();
    }
    @Test
    public void testGetResponse() {
        UserServiceScheduleProto.UsersResponse users = clientService.getResponse();

        assertAll(
                () -> assertEquals(1, users.getUsersCount()),
                () -> assertEquals(Role.USER.name(), users.getUsers(0).getRole()),
                () -> assertEquals(TEST_EMAIL, users.getUsers(0).getEmail()),
                () -> assertEquals(TEST_FNAME, users.getUsers(0).getFirstname()),
                () -> assertEquals(TEST_LNAME, users.getUsers(0).getLastname())
        );
    }

}
