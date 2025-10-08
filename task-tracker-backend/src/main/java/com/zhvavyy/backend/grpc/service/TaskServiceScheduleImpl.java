package com.zhvavyy.backend.grpc.service;

import com.my.grpc.task.TaskService;
import com.my.grpc.task.TaskServiceScheduleGrpc;
import com.zhvavyy.backend.dto.TaskDto;
import com.zhvavyy.backend.dto.TaskResponse;
import com.zhvavyy.backend.service.TaskServiceImpl;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.lognet.springboot.grpc.GRpcService;



@GRpcService
@RequiredArgsConstructor
public class TaskServiceScheduleImpl extends TaskServiceScheduleGrpc.TaskServiceScheduleImplBase {

    private final TaskServiceImpl taskService;

    @Override
    public void findAllByUserId(TaskService.SchedulerTasksRequest request, StreamObserver<TaskService.TaskResponse> responseObserver) {
        TaskResponse allByUserId = taskService.findAllByUserId(request.getId());

        TaskService.TaskResponse.Builder response = TaskService.TaskResponse.newBuilder();

        for (TaskDto task : allByUserId.tasks()) {
            TaskService.TaskDto taskDto = TaskService.TaskDto.newBuilder()
                    .setId(task.id())
                    .setTitle(task.title())
                    .setStatus(task.status().name())
                    .build();
            response.addTasks(taskDto);
        }
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }
}
