package com.zhvavyy.scheduler.service;

import com.zhvavyy.scheduler.kafka.messaging.dto.MessageForEmail;
import com.zhvavyy.scheduler.kafka.messaging.producer.UserReportProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ReportDispatcherService {

    private final TaskAggregationService agrService;
    private final UserReportProducer producer;

    public void dispatcherUserReport(){
     for(MessageForEmail message : agrService.buildUserTasksReport()){
      producer.sendMessage(message);
     }
    }
}
