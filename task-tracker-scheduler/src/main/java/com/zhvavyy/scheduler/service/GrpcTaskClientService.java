package com.zhvavyy.scheduler.service;


import com.zhvavyy.backend.grpc.TaskServiceScheduleGrpc;
import com.zhvavyy.backend.grpc.TaskServiceScheduleProto;
import com.zhvavyy.scheduler.service.interceptor.UserIdGrpcClientInterceptor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GrpcTaskClientService {

    @GrpcClient("task-service")
    private TaskServiceScheduleGrpc.TaskServiceScheduleBlockingStub stub;

    public List<TaskServiceScheduleProto.TaskDto> getResponseTask(Long userId) {

        UserIdGrpcClientInterceptor.USER_ID_CONTEXT.set(userId);
        try {
            TaskServiceScheduleProto.SchedulerTasksRequest request =
                    TaskServiceScheduleProto.SchedulerTasksRequest.newBuilder().build();

            return stub.findAllByUserId(request).getTasksList();
        } finally {
            UserIdGrpcClientInterceptor.USER_ID_CONTEXT.remove();
        }
    }
}