package com.zhvavyy.scheduler.unit.service;

import com.zhvavyy.backend.grpc.TaskServiceScheduleGrpc;
import com.zhvavyy.backend.grpc.TaskServiceScheduleProto;
import com.zhvavyy.scheduler.service.GrpcTaskClientService;
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
public class GrpcTaskClientServiceTest {

    @InjectMocks
    private GrpcTaskClientService clientService;
    @Mock
    private TaskServiceScheduleGrpc.TaskServiceScheduleBlockingStub blockingStub;

    @Test
    public void getResponseTask(){
        TaskServiceScheduleProto.TaskDto.Builder taskDto= TaskServiceScheduleProto.TaskDto.newBuilder()
                .setId(1);
        TaskServiceScheduleProto.TaskResponse response = TaskServiceScheduleProto.TaskResponse.newBuilder()
                .addTasks(taskDto)
                .build();

        TaskServiceScheduleProto.SchedulerTasksRequest request= TaskServiceScheduleProto.SchedulerTasksRequest
                .newBuilder()
                .setId(1)
                .build();
        when(blockingStub.findAllByUserId(request)).thenReturn(response);
        List<TaskServiceScheduleProto.TaskDto> result = clientService.getResponseTask(1L);
        assertEquals(1, result.size());
        assertEquals(taskDto.getEmail(), result.get(0).getEmail());
        verify(blockingStub).findAllByUserId(request);
    }
}
