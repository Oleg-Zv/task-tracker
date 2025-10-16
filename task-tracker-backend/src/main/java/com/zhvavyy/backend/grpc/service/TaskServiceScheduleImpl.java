package com.zhvavyy.backend.grpc.service;


import com.zhvavyy.backend.dto.TaskDto;
import com.zhvavyy.backend.dto.TaskResponse;
import com.zhvavyy.backend.grpc.TaskServiceScheduleGrpc;
import com.zhvavyy.backend.grpc.TaskServiceScheduleProto;

import com.zhvavyy.backend.grpc.mapper.TaskGrpcMapper;
import com.zhvavyy.backend.service.TaskService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;


@GrpcService
@RequiredArgsConstructor
public class TaskServiceScheduleImpl extends TaskServiceScheduleGrpc.TaskServiceScheduleImplBase {

    private final TaskService taskService;

    @Override
    public void findAllByUserId(TaskServiceScheduleProto.SchedulerTasksRequest request, StreamObserver<TaskServiceScheduleProto.TaskResponse> responseObserver) {
        TaskResponse allByUserId = taskService.findAllByUserId(request.getId());

        TaskServiceScheduleProto.TaskResponse.Builder response = TaskServiceScheduleProto.TaskResponse.newBuilder();

        for (TaskDto task : allByUserId.tasks()) {
            response.addTasks(TaskGrpcMapper.toProto(task));
        }
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }
}
