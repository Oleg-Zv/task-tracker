package com.zhvavyy.scheduler.service;


import com.zhvavyy.backend.grpc.UserServiceScheduleProto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserReportService {

    private final GrpcUserClientService grpcClientService;

    public List<UserServiceScheduleProto.UserDto> formingUsersList() {
        return grpcClientService.getResponse().getUsersList();
    }

    public List<Long> getUsersId() {
        List<UserServiceScheduleProto.UserDto> users = formingUsersList();
        List<Long> usersId = new ArrayList<>();
        for (UserServiceScheduleProto.UserDto user : users) {
            usersId.add(user.getId());
        }
        return usersId;
    }

}
