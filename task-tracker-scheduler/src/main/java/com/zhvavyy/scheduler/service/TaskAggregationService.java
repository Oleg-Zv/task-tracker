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
        Map<Long,List<TaskService.TaskDto>> tasksUserMap = getUserTasks(usersId);
        Map<Long, Map<String, Integer>> stats = countStatuses(tasksUserMap);
        Map<Long, StringBuilder> report = formingReports(tasksUserMap);
        return formingDto(report,stats,tasksUserMap);
    }


    public Map<Long,List<TaskService.TaskDto>> getUserTasks(List<Long>usersId){
      Map<Long, List<TaskService.TaskDto>> userTasksMap= new HashMap<>();
      for(Long userId: usersId){
          List<TaskService.TaskDto>tasks = grpcTaskClientService.getResponseTask(userId);
          userTasksMap.put(userId,tasks);
      }
      return userTasksMap;
    }


    public Map<Long,Map<String,Integer>> countStatuses(Map<Long,List<TaskService.TaskDto>> tasksUserMap){
        Map<Long, Map<String, Integer>> result = new HashMap<>();

        for(Map.Entry<Long,List<TaskService.TaskDto>>entry: tasksUserMap.entrySet()) {
            int done = 0;
            int pending = 0;

            for (TaskService.TaskDto task : entry.getValue()) {
                if (STATUS_DONE.equals(task.getStatus())) done++;
                else if (STATUS_PENDING.equals(task.getStatus())) pending++;
            }

            Map<String, Integer> statuses = new HashMap<>();
            statuses.put(STATUS_DONE, done);
            statuses.put(STATUS_PENDING, pending);

            result.put(entry.getKey(),statuses);
        }

       return result;
    }

    public Map<Long,StringBuilder> formingReports(Map<Long,List<TaskService.TaskDto>> tasksUserMap) {
        Map<Long, StringBuilder> reports = new HashMap<>();

        for (Map.Entry<Long, List<TaskService.TaskDto>> entry : tasksUserMap.entrySet()) {
            StringBuilder body = new StringBuilder("Задачи: \n");

            List<TaskService.TaskDto> done =
                    entry.getValue().stream()
                            .filter(t -> t.getStatus().equals(STATUS_DONE))
                            .limit(5)
                            .toList();

            List<TaskService.TaskDto> pending =
                    entry.getValue().stream()
                            .filter(t -> t.getStatus().equals(STATUS_PENDING))
                            .limit(5)
                            .toList();


            if (!done.isEmpty()) {
                done.forEach(t -> body.append("- ").append(t.getTitle()).append("\n"));
            }
            if (!pending.isEmpty()) {
                pending.forEach(t -> body.append("- ").append(t.getTitle()).append("\n"));

            }
            reports.put(entry.getKey(),body);
        }

        return reports;
    }


    public List<MessageForEmail> formingDto(Map<Long, StringBuilder> reportMap,
                                            Map<Long, Map<String, Integer>> statusesMap,
                                            Map<Long, List<TaskService.TaskDto>> tasksUserMap) {

        List<MessageForEmail> message = new ArrayList<>();

        for (Long userId : tasksUserMap.keySet()) {
            List<TaskService.TaskDto> tasks = tasksUserMap.get(userId);
            if (tasks.isEmpty()) continue;

            String email = tasks.get(0).getEmail();
            Map<String,Integer> stats= statusesMap.get(userId);
            StringBuilder report = reportMap.get(userId);

            int doneCount = stats.getOrDefault(STATUS_DONE, 0);
            int pendingCount = stats.getOrDefault(STATUS_PENDING, 0);

            if (doneCount > 0) {
                message.add(MessageForEmail.builder()
                        .recipient(email)
                        .msgBody(report.toString())
                        .subject("За сегодня вы выполнили " + doneCount + " задач!")
                        .build());
            }
            if (pendingCount > 0) {
                message.add(MessageForEmail.builder()
                        .recipient(email)
                        .msgBody(report.toString())
                        .subject("За сегодня " + pendingCount + " несделанных задач!")
                        .build());

            }
        }
        return message;
    }
}
