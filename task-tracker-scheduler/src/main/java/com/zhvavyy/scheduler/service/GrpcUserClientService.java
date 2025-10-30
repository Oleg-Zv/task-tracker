package com.zhvavyy.scheduler.service;


import com.zhvavyy.backend.grpc.UserServiceScheduleGrpc;
import com.zhvavyy.backend.grpc.UserServiceScheduleProto;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;


@Service
public class GrpcUserClientService {

    @GrpcClient("user-service")
    private UserServiceScheduleGrpc.UserServiceScheduleBlockingStub stub;

    public UserServiceScheduleProto.UsersResponse getResponse() {
        UserServiceScheduleProto.SchedulerUsersRequest request = UserServiceScheduleProto.SchedulerUsersRequest
                .newBuilder().build();

        return stub.getAll(request);
    }
}