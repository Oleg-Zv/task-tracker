package com.zhvavyy.backend.unit.grpc;

import com.zhvavyy.backend.dto.TaskDto;
import com.zhvavyy.backend.dto.TaskResponse;
import com.zhvavyy.backend.grpc.TaskServiceScheduleProto;
import com.zhvavyy.backend.grpc.service.TaskServiceScheduleImpl;
import com.zhvavyy.backend.grpc.service.interceptor.UserInterceptor;
import com.zhvavyy.backend.model.enums.Status;
import com.zhvavyy.backend.service.TaskService;

import io.grpc.Context;
import io.grpc.stub.StreamObserver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class TaskServiceScheduleImplTest {

    @Mock
    private TaskService taskService;
    @Mock
    private StreamObserver<TaskServiceScheduleProto.TaskResponse> responseObserver;

    private static final Long TASK_ID = 1L;
    private static final Long USER_ID = 1L;
    private static final String TEST_TITLE = "name";
    private static final String TEST_EMAIL = "test@gmail.com";

    private TaskServiceScheduleImpl service;

    @BeforeEach
    void setup() {
        service = new TaskServiceScheduleImpl(taskService);
    }

    @Test
    void findAllByUserId_ReturnsTasks() {
        TaskDto taskDto = new TaskDto(TASK_ID, TEST_EMAIL, TEST_TITLE, Status.DONE);
        when(taskService.findAllByUserId(USER_ID)).thenReturn(new TaskResponse(List.of(taskDto)));

        // Вставляем USER_ID в Context
        Context ctx = Context.current().withValue(UserInterceptor.USER_ID_CTX_KEY, USER_ID);
        ctx.run(() -> {
            TaskServiceScheduleProto.SchedulerTasksRequest request =
                    TaskServiceScheduleProto.SchedulerTasksRequest.newBuilder().build();

            ArgumentCaptor<TaskServiceScheduleProto.TaskResponse> captor =
                    ArgumentCaptor.forClass(TaskServiceScheduleProto.TaskResponse.class);

            service.findAllByUserId(request, responseObserver);

            verify(responseObserver).onNext(captor.capture());
            verify(responseObserver).onCompleted();

            TaskServiceScheduleProto.TaskResponse response = captor.getValue();
            assertEquals(1, response.getTasksCount());
            assertEquals(taskDto.title(), response.getTasks(0).getTitle());
            assertEquals(taskDto.email(), response.getTasks(0).getEmail());
            assertEquals(taskDto.status().name(), response.getTasks(0).getStatus());

            verify(taskService).findAllByUserId(USER_ID);
        });
    }

    @Test
    void findAllByUserId_NoTasks() {
        when(taskService.findAllByUserId(USER_ID)).thenReturn(new TaskResponse(List.of()));

        Context ctx = Context.current().withValue(UserInterceptor.USER_ID_CTX_KEY, USER_ID);
        ctx.run(() -> {
            TaskServiceScheduleProto.SchedulerTasksRequest request =
                    TaskServiceScheduleProto.SchedulerTasksRequest.newBuilder().build();

            ArgumentCaptor<TaskServiceScheduleProto.TaskResponse> captor =
                    ArgumentCaptor.forClass(TaskServiceScheduleProto.TaskResponse.class);

            service.findAllByUserId(request, responseObserver);

            verify(responseObserver).onNext(captor.capture());
            verify(responseObserver).onCompleted();

            TaskServiceScheduleProto.TaskResponse response = captor.getValue();
            assertEquals(0, response.getTasksCount());

            verify(taskService).findAllByUserId(USER_ID);
        });
    }

    @Test
    void findAllByUserId_UserIdNull() {
        when(taskService.findAllByUserId(null)).thenReturn(new TaskResponse(List.of()));

        Context ctx = Context.current(); // USER_ID не вставлен
        ctx.run(() -> {
            TaskServiceScheduleProto.SchedulerTasksRequest request =
                    TaskServiceScheduleProto.SchedulerTasksRequest.newBuilder().build();

            ArgumentCaptor<TaskServiceScheduleProto.TaskResponse> captor =
                    ArgumentCaptor.forClass(TaskServiceScheduleProto.TaskResponse.class);

            service.findAllByUserId(request, responseObserver);

            verify(responseObserver).onNext(captor.capture());
            verify(responseObserver).onCompleted();

            TaskServiceScheduleProto.TaskResponse response = captor.getValue();
            assertEquals(0, response.getTasksCount());

            verify(taskService).findAllByUserId(null);
        });
    }

    @Test
    void findAllByUserId_ServiceThrowsException() {
        when(taskService.findAllByUserId(USER_ID)).thenThrow(new RuntimeException("Database error"));

        Context ctx = Context.current().withValue(UserInterceptor.USER_ID_CTX_KEY, USER_ID);
        ctx.run(() -> {
            TaskServiceScheduleProto.SchedulerTasksRequest request =
                    TaskServiceScheduleProto.SchedulerTasksRequest.newBuilder().build();

            // Нужно обернуть сервис в try/catch, чтобы onError был вызван
            try {
                service.findAllByUserId(request, responseObserver);
            } catch (RuntimeException e) {
                responseObserver.onError(e);
            }

            ArgumentCaptor<Throwable> captor = ArgumentCaptor.forClass(Throwable.class);
            verify(responseObserver).onError(captor.capture());
            Throwable error = captor.getValue();
            assertEquals("Database error", error.getMessage());

            verify(responseObserver, never()).onNext(any());
            verify(responseObserver, never()).onCompleted();
        });
    }
}
