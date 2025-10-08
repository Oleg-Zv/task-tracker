package com.zhvavyy.scheduler.service;

import com.my.grpc.task.TaskService;
import com.my.grpc.task.TaskServiceScheduleGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GrpcTaskClientService {

    @GrpcClient("task-service")
    private TaskServiceScheduleGrpc.TaskServiceScheduleBlockingStub stub;


    public List<TaskService.TaskDto> getResponseTask(Long userId){
        TaskService.SchedulerTasksRequest request= TaskService.SchedulerTasksRequest
                .newBuilder()
                .setId(userId)
                .build();

        return stub.findAllByUserId(request).getTasksList();
    }
}