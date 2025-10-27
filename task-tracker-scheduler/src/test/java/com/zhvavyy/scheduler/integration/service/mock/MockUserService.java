package com.zhvavyy.scheduler.integration.service.mock;

import com.zhvavyy.backend.grpc.UserServiceScheduleGrpc;
import com.zhvavyy.backend.grpc.UserServiceScheduleProto;
import com.zhvavyy.scheduler.integration.enums.Role;
import io.grpc.stub.StreamObserver;

public class MockUserService extends UserServiceScheduleGrpc.UserServiceScheduleImplBase {

    private static final String TEST_EMAIL = "grpc@gmail.com";
    private static final String TEST_FNAME = "Poly";
    private static final String TEST_LNAME = "Sorry";

    @Override
    public void getAll(UserServiceScheduleProto.SchedulerUsersRequest request,
                       StreamObserver<UserServiceScheduleProto.UsersResponse> responseObserver){

        UserServiceScheduleProto.UsersResponse response = UserServiceScheduleProto.UsersResponse.newBuilder()
                .addUsers(UserServiceScheduleProto.UserDto.newBuilder()
                        .setEmail(TEST_EMAIL)
                        .setFirstname(TEST_FNAME)
                        .setLastname(TEST_LNAME)
                        .setRole(Role.USER.name())
                )
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
