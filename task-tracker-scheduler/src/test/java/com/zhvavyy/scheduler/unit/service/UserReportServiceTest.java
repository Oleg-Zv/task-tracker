package com.zhvavyy.scheduler.unit.service;

import com.zhvavyy.backend.grpc.UserServiceScheduleProto;
import com.zhvavyy.scheduler.service.GrpcUserClientService;
import com.zhvavyy.scheduler.service.UserReportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserReportServiceTest {

    @InjectMocks
    private UserReportService userReportService;
    @Mock
    private GrpcUserClientService userClientService;

    @Test
    public void formingUsersList(){
        UserServiceScheduleProto.UserDto userDto = UserServiceScheduleProto.UserDto.newBuilder()
                        .build();
        UserServiceScheduleProto.UsersResponse response = UserServiceScheduleProto.UsersResponse.newBuilder().addUsers(userDto).build();

        when(userClientService.getResponse()).thenReturn(response);
        List<UserServiceScheduleProto.UserDto> result = userReportService.formingUsersList();
        assertEquals(1,result.size());
        verify(userClientService).getResponse();
    }

    @Test
    public void getUsersId(){
        UserServiceScheduleProto.UserDto userDto = UserServiceScheduleProto.UserDto.newBuilder()
                .setId(2L).build();
        UserServiceScheduleProto.UsersResponse response = UserServiceScheduleProto.UsersResponse.newBuilder().addUsers(userDto).build();

        when(userClientService.getResponse()).thenReturn(response);
        List<Long> usersId = userReportService.getUsersId();
        assertEquals(1, usersId.size());
        assertEquals(userDto.getId(), usersId.get(0));
        verify(userClientService).getResponse();
    }
}
