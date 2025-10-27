package com.zhvavyy.backend.integration.grpc;

import com.zhvavyy.backend.grpc.UserServiceScheduleGrpc;
import com.zhvavyy.backend.grpc.UserServiceScheduleProto;
import com.zhvavyy.backend.integration.BaseIntegrationTest;
import com.zhvavyy.backend.model.User;
import com.zhvavyy.backend.model.enums.Role;
import com.zhvavyy.backend.repository.UserRepository;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = {
        "grpc.server.port=-1",
        "grpc.server.in-process-name=test-server"
})
@DirtiesContext
public class UserServiceScheduleImplIT extends BaseIntegrationTest {

    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();


    @Autowired
    private UserRepository userRepository;
    private UserServiceScheduleGrpc.UserServiceScheduleBlockingStub stub;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String TEST_EMAIL = "grpc@gmail.com";
    private static final String TEST_FNAME = "Poly";
    private static final String TEST_LNAME = "Sorry";
    private static final String TEST_PASS = "myPass21";

    @BeforeEach
    public void init() {
        String serverName = "test-server";
        stub = UserServiceScheduleGrpc.newBlockingStub(
                InProcessChannelBuilder.forName(serverName)
                        .directExecutor()
                        .build()
        );
        userRepository.deleteAll();
        userRepository.save(User.builder()
                .email(TEST_EMAIL)
                .role(Role.USER)
                .firstname(TEST_FNAME)
                .lastname(TEST_LNAME)
                .password(passwordEncoder.encode(TEST_PASS))
                .build());

    }

    @Test
    public void getAllUsers_whenUsersExist() {

        UserServiceScheduleProto.SchedulerUsersRequest request = UserServiceScheduleProto.SchedulerUsersRequest.newBuilder().build();

        UserServiceScheduleProto.UsersResponse response = stub.getAll(request);

        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(1, response.getUsersCount()),
                () -> assertEquals(TEST_EMAIL, response.getUsers(0).getEmail()),
                ()->assertEquals(TEST_FNAME, response.getUsers(0).getFirstname()),
                ()->assertEquals(TEST_LNAME, response.getUsers(0).getLastname()),
                ()->assertEquals(Role.USER.name(), response.getUsers(0).getRole())
        );
    }

    @Test
    public void getAllUsers_whenNotUsers() {
        userRepository.deleteAll();
        UserServiceScheduleProto.SchedulerUsersRequest request = UserServiceScheduleProto.SchedulerUsersRequest.newBuilder().build();

        UserServiceScheduleProto.UsersResponse response = stub.getAll(request);

        assertAll(
                () -> assertTrue(response.getUsersList().isEmpty()),
                () -> assertEquals(0, response.getUsersCount())
        );
    }

    @AfterEach
    public void cleanUp() {
        userRepository.deleteAll();
    }
}