package com.zhvavyy.scheduler.integration.service.mock;

import com.zhvavyy.backend.grpc.TaskServiceScheduleGrpc;
import com.zhvavyy.backend.grpc.TaskServiceScheduleProto;
import io.grpc.stub.StreamObserver;

public class MockTaskService extends TaskServiceScheduleGrpc.TaskServiceScheduleImplBase {

    private static final String TEST_EMAIL = "grpc@gmail.com";
    private static final String TEST_TITLE = "my task";

    @Override
    public void findAllByUserId(TaskServiceScheduleProto.SchedulerTasksRequest request,
                                StreamObserver<TaskServiceScheduleProto.TaskResponse> responseObserver) {
        TaskServiceScheduleProto.TaskResponse response =
                TaskServiceScheduleProto.TaskResponse.newBuilder()
                        .addTasks(TaskServiceScheduleProto.TaskDto.newBuilder()
                                .setTitle(TEST_TITLE)
                                .setEmail(TEST_EMAIL)
                                .build())
                        .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}