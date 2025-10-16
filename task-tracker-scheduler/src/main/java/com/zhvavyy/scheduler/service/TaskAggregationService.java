package com.zhvavyy.scheduler.service;


import com.zhvavyy.backend.grpc.TaskServiceScheduleProto;
import com.zhvavyy.scheduler.dto.Statuses;
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
        Map<Long,List<TaskServiceScheduleProto.TaskDto>> tasksUserMap = getUserTasks(usersId);
        Map<Long, Map<String, Integer>> stats = countStatuses(tasksUserMap);
        Map<Long, Statuses> report = formingReports(tasksUserMap);
        return formingDto(report,stats,tasksUserMap);
    }


    public Map<Long,List<TaskServiceScheduleProto.TaskDto>> getUserTasks(List<Long>usersId){
      Map<Long, List<TaskServiceScheduleProto.TaskDto>> userTasksMap= new HashMap<>();
      for(Long userId: usersId){
          List<TaskServiceScheduleProto.TaskDto>tasks = grpcTaskClientService.getResponseTask(userId);
          userTasksMap.put(userId,tasks);
      }
      return userTasksMap;
    }


    public Map<Long,Map<String,Integer>> countStatuses(Map<Long,List<TaskServiceScheduleProto.TaskDto>> tasksUserMap){
        Map<Long, Map<String, Integer>> result = new HashMap<>();

        for(Map.Entry<Long,List<TaskServiceScheduleProto.TaskDto>>entry: tasksUserMap.entrySet()) {
            int done = 0;
            int pending = 0;

            for (TaskServiceScheduleProto.TaskDto task : entry.getValue()) {
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

    public Map<Long,Statuses> formingReports(Map<Long,List<TaskServiceScheduleProto.TaskDto>> tasksUserMap) {
        Map<Long, Statuses> reports = new HashMap<>();

        for (Map.Entry<Long, List<TaskServiceScheduleProto.TaskDto>> entry : tasksUserMap.entrySet()) {
            StringBuilder bodyDone = new StringBuilder();
            StringBuilder bodyPending = new StringBuilder();
            StringBuilder bodyCombined = new StringBuilder();


            List<TaskServiceScheduleProto.TaskDto> done =
                    entry.getValue().stream()
                            .filter(t -> t.getStatus().equals(STATUS_DONE))
                            .limit(5)
                            .toList();

            List<TaskServiceScheduleProto.TaskDto> pending =
                    entry.getValue().stream()
                            .filter(t -> t.getStatus().equals(STATUS_PENDING))
                            .limit(5)
                            .toList();


            if (!done.isEmpty()) {
                done.forEach(t -> bodyDone.append("- ").append(t.getTitle()).append("\n"));
            }
            if (!pending.isEmpty()) {
                pending.forEach(t -> bodyPending.append("- ").append(t.getTitle()).append("\n"));
            }
            if(!done.isEmpty() && !pending.isEmpty()){
                bodyCombined.append("Выполненные задачи:\n");
                done.forEach(t -> bodyCombined.append("- ").append(t.getTitle()).append("\n"));

                bodyCombined.append("\nНевыполненные задачи:\n");
                pending.forEach(t -> bodyCombined.append("- ").append(t.getTitle()).append("\n"));
            }
            reports.put(entry.getKey(),
                    new Statuses(bodyDone.toString(),bodyPending.toString(),bodyCombined.toString()));
        }

        return reports;
    }


    public List<MessageForEmail> formingDto(Map<Long, Statuses> reportMap,
                                            Map<Long, Map<String, Integer>> statusesMap,
                                            Map<Long, List<TaskServiceScheduleProto.TaskDto>> tasksUserMap) {

        List<MessageForEmail> message = new ArrayList<>();

        for (Long userId : tasksUserMap.keySet()) {
            List<TaskServiceScheduleProto.TaskDto> tasks = tasksUserMap.get(userId);
            if (tasks.isEmpty()) continue;

            String email = tasks.get(0).getEmail();
            Map<String,Integer> stats= statusesMap.get(userId);
            Statuses reportStatus = reportMap.get(userId);

            int doneCount = stats.getOrDefault(STATUS_DONE, 0);
            int pendingCount = stats.getOrDefault(STATUS_PENDING, 0);

            if(doneCount>0 && pendingCount>0){
                message.add(MessageForEmail.builder()
                        .recipient(email)
                        .msgBody(reportStatus.combinedBody())
                        .subject("Итоги дня: \n" +
                                "Задач выполнено: " + doneCount+
                                "\nНесделанных задач: "+ pendingCount)
                        .build());
            }
            else if (doneCount > 0) {
                message.add(MessageForEmail.builder()
                        .recipient(email)
                        .msgBody(reportStatus.doneBody())
                        .subject("Выполненных задач за сегодня: " + doneCount)
                        .build());
            }
           else if (pendingCount > 0) {
                message.add(MessageForEmail.builder()
                        .recipient(email)
                        .msgBody(reportStatus.pendingBody())
                        .subject("Кол-во несделанных задач: " + pendingCount)
                        .build());
            }
        }
        return message;
    }
}
