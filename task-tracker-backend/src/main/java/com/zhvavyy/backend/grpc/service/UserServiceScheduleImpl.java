package com.zhvavyy.backend.grpc.service;


import com.zhvavyy.backend.dto.UserReadDto;
import com.zhvavyy.backend.grpc.UserServiceScheduleGrpc;
import com.zhvavyy.backend.grpc.UserServiceScheduleProto;
import com.zhvavyy.backend.grpc.mapper.UserGrpcMapper;
import com.zhvavyy.backend.service.UserService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;

@GrpcService
@RequiredArgsConstructor
public class UserServiceScheduleImpl extends UserServiceScheduleGrpc.UserServiceScheduleImplBase {

    private  final UserService userService;

    @Override
    public void getAll(UserServiceScheduleProto.SchedulerUsersRequest request, StreamObserver<UserServiceScheduleProto.UsersResponse> responseObserver) {
        List<UserReadDto> users = userService.getAll();
        UserServiceScheduleProto.UsersResponse.Builder response = UserServiceScheduleProto.UsersResponse
                .newBuilder();

        for(UserReadDto user: users){
            response.addUsers(UserGrpcMapper.toProto(user));
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }
}
