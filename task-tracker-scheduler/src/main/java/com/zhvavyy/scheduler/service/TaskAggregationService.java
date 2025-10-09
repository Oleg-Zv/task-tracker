package com.zhvavyy.scheduler.service;

import com.my.grpc.task.TaskService;

import com.zhvavyy.scheduler.kafka.messaging.dto.MessageForEmail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TaskAggregationService {

    private final GrpcTaskClientService grpcTaskClientService;
    private final UserReportService userReportService;
    private final String STATUS_DONE= "DONE";
    private final String STATUS_PENDING= "PENDING";

    public List<MessageForEmail> buildUserTasksReport(){
        List<Long> usersId = userReportService.getUsersId();
        List<TaskService.TaskDto> tasks = getUserTasks(usersId);
        Map<String,Integer> stats = countStatuses(tasks);
        return formingDto(stats,tasks);
    }

    public List<TaskService.TaskDto> getUserTasks(List<Long>usersId){
        List<TaskService.TaskDto> tasks=new ArrayList<>();
        for(Long id : usersId){
        tasks.addAll(grpcTaskClientService.getResponseTask(id));
        }
        return tasks;
    }

    public Map<String,Integer> countStatuses(List<TaskService.TaskDto> tasks){
        int countDone=0;
        int countPending=0;
        Map<String, Integer> statuses = new HashMap<>();
        for (TaskService.TaskDto task: tasks){
            if(task.getStatus().equals(STATUS_DONE)){
                countDone++;
            }
            else if(task.getStatus().equals(STATUS_PENDING)){
                countPending++;
            }
        }
        statuses.put(STATUS_DONE, countDone);
        statuses.put(STATUS_PENDING, countPending);

       return statuses;
    }

    public List<MessageForEmail> formingDto(Map<String,Integer>st, List<TaskService.TaskDto> tasks){
        List<MessageForEmail> message = new ArrayList<>();
        for(Map.Entry<String,Integer>entry: st.entrySet()) {
            for (TaskService.TaskDto task : tasks) {
             if(entry.getKey().equals(STATUS_DONE) && entry.getValue()>=1){
                 message.add(MessageForEmail.builder()
                         .recipient(task.getEmail())
                         .msgBody("Задача: " + task.getTitle())
                         .subject("За сегодня вы выполнили "+ entry.getValue() + " задач!")
                         .build());
             }
             if(entry.getKey().equals(STATUS_PENDING) && entry.getValue()>=1){
                 message.add(MessageForEmail.builder()
                         .recipient(task.getEmail())
                         .msgBody("Задача: " + task.getTitle())
                         .subject("За сегодня "+ entry.getValue() + " несделанных задач!")
                         .build());
             }
            }
        }
        return message;
    }
}
