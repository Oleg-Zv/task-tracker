package com.zhvavyy.scheduler.service;

import com.my.grpc.user.UserService;

import com.zhvavyy.scheduler.kafka.messaging.dto.MessageForEmail;
import com.zhvavyy.scheduler.kafka.messaging.mapper.UserMessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserReportService {

    private final GrpcUserClientService grpcClientService;
    private final UserMessageMapper listDto;
    private final GrpcTaskClientService taskClientService;

    public List<UserService.UserDto> formingUsersList() {
        return grpcClientService.getResponse().getUsersList();
    }

    public List<MessageForEmail> formingDto(){
        List<UserService.UserDto> users=formingUsersList();
        return listDto.mapTo(users);
    }

    public void userId(){
        for(UserService.UserDto user: formingUsersList()){
            taskClientService.getResponseTask(user.getId());
        }
    }
}
