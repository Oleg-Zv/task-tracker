package com.zhvavyy.scheduler.service;

import com.my.grpc.user.UserService;
import com.my.grpc.user.UserServiceScheduleGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;


@Service
public class GrpcUserClientService {

    @GrpcClient("user-service")
    private UserServiceScheduleGrpc.UserServiceScheduleBlockingStub stub;

    public UserService.UsersResponse getResponse() {
        UserService.SchedulerUsersRequest request = UserService.SchedulerUsersRequest
                .newBuilder().build();

        return stub.getAll(request);
    }
}
