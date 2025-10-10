package com.zhvavyy.backend.grpc.service;


import com.my.grpc.user.UserService;
import com.my.grpc.user.UserServiceScheduleGrpc;
import com.zhvavyy.backend.dto.UserReadDto;
import com.zhvavyy.backend.service.UserServiceImpl;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;

@GrpcService
@RequiredArgsConstructor
public class UserServiceScheduleImpl extends UserServiceScheduleGrpc.UserServiceScheduleImplBase {

    private  final UserServiceImpl userService;
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
