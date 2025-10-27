package com.zhvavyy.backend.unit.grpc;

import com.zhvavyy.backend.dto.UserReadDto;
import com.zhvavyy.backend.grpc.UserServiceScheduleProto;
import com.zhvavyy.backend.grpc.service.UserServiceScheduleImpl;
import com.zhvavyy.backend.model.enums.Role;
import com.zhvavyy.backend.service.UserService;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SuppressWarnings("FieldCanBeLocal")
@ExtendWith(MockitoExtension.class)
public class UserServiceScheduleImplTest {

    @Mock
    private UserService userService;
    @Mock
    private StreamObserver<UserServiceScheduleProto.UsersResponse> responseObserver;

    private final Long USER_ID= 1L;
    private final String TEST_EMAIL= "test@gmail.com";
    private final String TEST_FNAME= "test";
    private final String TEST_LNAME= "test";

    @Test
    public void getAll() {
        UserReadDto userReadDto = new UserReadDto(USER_ID, TEST_EMAIL, Role.USER, TEST_FNAME, TEST_LNAME);

        when(userService.getAll()).thenReturn(List.of(userReadDto));

        UserServiceScheduleImpl userServiceSchedule = new UserServiceScheduleImpl(userService);
        UserServiceScheduleProto.SchedulerUsersRequest request = UserServiceScheduleProto.SchedulerUsersRequest
                .newBuilder()
                .build();

        ArgumentCaptor<UserServiceScheduleProto.UsersResponse> captor = ArgumentCaptor.forClass(UserServiceScheduleProto.UsersResponse.class);

        userServiceSchedule.getAll(request, responseObserver);
        verify(responseObserver).onNext(captor.capture());
        verify(responseObserver).onCompleted();
        verify(userService).getAll();

        UserServiceScheduleProto.UsersResponse response = captor.getValue();
        assertEquals(1, response.getUsersList().size());
        assertEquals(userReadDto.email(), response.getUsersList().get(0).getEmail());
        assertEquals(userReadDto.role().getAuthority(), response.getUsersList().get(0).getRole());
        assertEquals(userReadDto.firstname(), response.getUsersList().get(0).getFirstname());
        assertEquals(userReadDto.lastname(), response.getUsersList().get(0).getLastname());

    }

    @Test
    public void getAllFailed_whenNoUsers() {
        when(userService.getAll()).thenReturn(List.of());

        UserServiceScheduleImpl userServiceSchedule = new UserServiceScheduleImpl(userService);
        UserServiceScheduleProto.SchedulerUsersRequest request = UserServiceScheduleProto.SchedulerUsersRequest
                .newBuilder()
                .build();
        ArgumentCaptor<UserServiceScheduleProto.UsersResponse> captor = ArgumentCaptor.forClass(UserServiceScheduleProto.UsersResponse.class);
        userServiceSchedule.getAll(request, responseObserver);

        verify(responseObserver).onNext(captor.capture());
        verify(responseObserver).onCompleted();
        verify(userService).getAll();

        UserServiceScheduleProto.UsersResponse response = captor.getValue();
        assertEquals(0, response.getUsersList().size());

    }
}
