package com.zhvavyy.scheduler.unit.service;

import com.zhvavyy.backend.grpc.UserServiceScheduleGrpc;
import com.zhvavyy.backend.grpc.UserServiceScheduleProto;
import com.zhvavyy.scheduler.service.GrpcUserClientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GrpcUserClientServiceTest {

    @InjectMocks
    private GrpcUserClientService clientService;
    @Mock
    private UserServiceScheduleGrpc.UserServiceScheduleBlockingStub blockingStub;

    @Test
    public void getResponseTask() {
        UserServiceScheduleProto.UserDto userDto = UserServiceScheduleProto.UserDto.newBuilder().build();
        UserServiceScheduleProto.UsersResponse response = UserServiceScheduleProto.UsersResponse.newBuilder()
                .addUsers(userDto)
                .build();
        UserServiceScheduleProto.SchedulerUsersRequest request = UserServiceScheduleProto.SchedulerUsersRequest
                .newBuilder()
                .build();
        when(blockingStub.getAll(request)).thenReturn(response);
        UserServiceScheduleProto.UsersResponse result = clientService.getResponse();
        assertEquals(userDto.getEmail(), result.getUsers(0).getEmail());
        verify(blockingStub).getAll(request);
    }
}
