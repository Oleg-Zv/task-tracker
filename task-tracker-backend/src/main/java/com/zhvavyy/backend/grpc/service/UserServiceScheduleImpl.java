package com.zhvavyy.backend.grpc.service;


import com.my.grpc.user.UserService;
import com.my.grpc.user.UserServiceScheduleGrpc;
import com.zhvavyy.backend.dto.UserReadDto;
import com.zhvavyy.backend.service.UserServiceImpl;
import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService;

import java.util.List;

@GRpcService
public class UserServiceScheduleImpl extends UserServiceScheduleGrpc.UserServiceScheduleImplBase {

    UserServiceImpl userService;
    @Override
    public void getAll(UserService.SchedulerUsersRequest request, StreamObserver<UserService.UsersResponse> responseObserver) {
        List<UserReadDto> users = userService.getAll();
        UserService.UsersResponse.Builder response = UserService.UsersResponse
                .newBuilder();

        for(UserReadDto user: users){
            UserService.UserDto userDto =
                    UserService.UserDto.newBuilder()
                            .setId(user.id())
                            .setEmail(user.email())
                            .setRole(user.role().getAuthority())
                            .setFirstname(user.firstname())
                            .setLastname(user.lastname())
                            .build();
            response.addUsers(userDto);
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }
}
