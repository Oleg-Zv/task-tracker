package com.zhvavyy.unit.grpc;

import com.zhvavyy.backend.dto.TaskDto;
import com.zhvavyy.backend.dto.TaskResponse;
import com.zhvavyy.backend.grpc.TaskServiceScheduleProto;
import com.zhvavyy.backend.grpc.service.TaskServiceScheduleImpl;
import com.zhvavyy.backend.model.enums.Status;
import com.zhvavyy.backend.service.TaskService;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SuppressWarnings("FieldCanBeLocal")
@ExtendWith(MockitoExtension.class)
public class TaskServiceScheduleImplTest {

    @Mock
    private TaskService taskService;
    @Mock
    private StreamObserver<TaskServiceScheduleProto.TaskResponse> responseObserver;

    private final Long TASK_ID= 1L;
    private final Long USER_ID= 1L;
    private final String TEST_TITLE= "name";
    private final String TEST_EMAIL= "test@gmail.com";

    @Test
    public void findAllByUserId() {
        TaskDto taskDto = new TaskDto(TASK_ID, TEST_EMAIL, TEST_TITLE, Status.DONE);

        when(taskService.findAllByUserId(USER_ID)).thenReturn(new TaskResponse(List.of(taskDto)));

        TaskServiceScheduleImpl taskServiceSchedule = new TaskServiceScheduleImpl(taskService);
        TaskServiceScheduleProto.SchedulerTasksRequest request = TaskServiceScheduleProto.SchedulerTasksRequest
                .newBuilder()
                .setId(USER_ID)
                .build();

        ArgumentCaptor<TaskServiceScheduleProto.TaskResponse> captor = ArgumentCaptor.forClass(TaskServiceScheduleProto.TaskResponse.class);

        taskServiceSchedule.findAllByUserId(request, responseObserver);
        verify(responseObserver).onNext(captor.capture());
        verify(responseObserver).onCompleted();

        TaskServiceScheduleProto.TaskResponse response = captor.getValue();
        assertEquals(1, response.getTasksCount());
        assertEquals(taskDto.title(), response.getTasks(0).getTitle());
        assertEquals(taskDto.email(), response.getTasks(0).getEmail());
        assertEquals(taskDto.status().name(), response.getTasks(0).getStatus());


        verify(taskService).findAllByUserId(USER_ID);

    }

    @Test
    public void findAllByUserId_UserNotFound() {
        when(taskService.findAllByUserId(USER_ID)).thenReturn(new TaskResponse(List.of()));

        TaskServiceScheduleImpl taskServiceSchedule = new TaskServiceScheduleImpl(taskService);
        TaskServiceScheduleProto.SchedulerTasksRequest request = TaskServiceScheduleProto.SchedulerTasksRequest
                .newBuilder()
                .setId(TASK_ID)
                .build();

        ArgumentCaptor<TaskServiceScheduleProto.TaskResponse> captor = ArgumentCaptor.forClass(TaskServiceScheduleProto.TaskResponse.class);

        taskServiceSchedule.findAllByUserId(request, responseObserver);
        verify(responseObserver).onNext(captor.capture());
        verify(responseObserver).onCompleted();
        verify(taskService).findAllByUserId(USER_ID);

        TaskServiceScheduleProto.TaskResponse response = captor.getValue();
        assertEquals(0, response.getTasksCount());

    }
}
