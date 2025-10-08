package com.zhvavyy.scheduler.service;

import com.my.grpc.user.UserService;
import com.my.grpc.user.UserServiceScheduleGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SchedulerClientServiceImpl implements SchedulerService {

@GrpcClient("user-service")
   private UserServiceScheduleGrpc.UserServiceScheduleBlockingStub stub;


    @Scheduled(cron = "${task.cron.expression}" )
    @Override
    public void scheduleUserReport() {
        UserService.SchedulerUsersRequest request= UserService.SchedulerUsersRequest
                .newBuilder().build();

       UserService.UsersResponse response =  stub.getAll(request);

        List<UserService.UserDto> users = response.getUsersList();
        System.out.println(response);

    }
}
