package com.zhvavyy.scheduler.service;


import com.zhvavyy.backend.grpc.TaskServiceScheduleGrpc;
import com.zhvavyy.backend.grpc.TaskServiceScheduleProto;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GrpcTaskClientService {

    @GrpcClient("task-service")
    private TaskServiceScheduleGrpc.TaskServiceScheduleBlockingStub stub;


    public List<TaskServiceScheduleProto.TaskDto> getResponseTask(Long userId){
        TaskServiceScheduleProto.SchedulerTasksRequest request= TaskServiceScheduleProto.SchedulerTasksRequest
                .newBuilder()
                .setId(userId)
                .build();

        return stub.findAllByUserId(request).getTasksList();
    }
}