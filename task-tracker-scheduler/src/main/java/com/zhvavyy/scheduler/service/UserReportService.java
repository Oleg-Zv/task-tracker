package com.zhvavyy.scheduler.service;

import com.my.grpc.user.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserReportService {

    private final GrpcUserClientService grpcClientService;

    public List<UserService.UserDto> formingUsersList() {
        return grpcClientService.getResponse().getUsersList();
    }

    public List<Long> getUsersId() {
        List<UserService.UserDto> users = formingUsersList();
        List<Long> usersId = new ArrayList<>();
        for (UserService.UserDto user : users) {
            usersId.add(user.getId());
        }
        return usersId;
    }

}
