package com.zhvavyy.scheduler.unit.service;

import com.zhvavyy.backend.grpc.TaskServiceScheduleGrpc;
import com.zhvavyy.backend.grpc.TaskServiceScheduleProto;
import com.zhvavyy.scheduler.service.GrpcTaskClientService;
import com.zhvavyy.scheduler.service.interceptor.UserIdGrpcClientInterceptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
public class GrpcTaskClientServiceTest {

    @InjectMocks
    private GrpcTaskClientService clientService;

    @Mock
    private TaskServiceScheduleGrpc.TaskServiceScheduleBlockingStub blockingStub;

    @Test
    void getResponseTask_ShouldReturnTasks() {
        Long userId = 1L;

        TaskServiceScheduleProto.TaskDto taskDto = TaskServiceScheduleProto.TaskDto.newBuilder()
                .setId(1)
                .setTitle("Test task")
                .setEmail("test@gmail.com")
                .setStatus("DONE")
                .build();

        TaskServiceScheduleProto.TaskResponse response = TaskServiceScheduleProto.TaskResponse.newBuilder()
                .addTasks(taskDto)
                .build();

        TaskServiceScheduleProto.SchedulerTasksRequest expectedRequest = TaskServiceScheduleProto.SchedulerTasksRequest.newBuilder().build();
        when(blockingStub.findAllByUserId(expectedRequest)).thenReturn(response);

        var result = clientService.getResponseTask(userId);

        assertEquals(1, result.size());
        assertEquals("Test task", result.get(0).getTitle());
        assertEquals("test@gmail.com", result.get(0).getEmail());
        assertEquals("DONE", result.get(0).getStatus());

        verify(blockingStub).findAllByUserId(expectedRequest);

        assertNull(UserIdGrpcClientInterceptor.USER_ID_CONTEXT.get());
    }
}
