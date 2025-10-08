package com.zhvavyy.scheduler.service;

import com.zhvavyy.scheduler.kafka.messaging.dto.MessageForEmail;
import com.zhvavyy.scheduler.kafka.messaging.producer.UserReportProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDispatcherService {

    private final UserReportService userReportService;
    private final UserReportProducer producer;

    public void dispatcherUserReport(){
        List<MessageForEmail> messageList = userReportService.formingDto();
        for(MessageForEmail dto : messageList){
            producer.sendMessage(dto);
        }
    }
}
