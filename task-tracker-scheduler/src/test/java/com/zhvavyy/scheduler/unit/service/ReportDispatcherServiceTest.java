package com.zhvavyy.scheduler.unit.service;

import com.zhvavyy.scheduler.kafka.messaging.dto.MessageForEmail;
import com.zhvavyy.scheduler.kafka.messaging.producer.UserReportProducer;
import com.zhvavyy.scheduler.service.ReportDispatcherService;
import com.zhvavyy.scheduler.service.TaskAggregationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReportDispatcherServiceTest {

    @InjectMocks
    private ReportDispatcherService dispatcherService;
    @Mock
    private TaskAggregationService aggregationService;
    @Mock
    private UserReportProducer producer;

    @Test
    public void dispatcherUserReport(){
        MessageForEmail message= new MessageForEmail();
        message.setRecipient("user@gmail.com");
        message.setMsgBody("test");
        message.setSubject("test");
        when(aggregationService.buildUserTasksReport()).thenReturn(List.of(message));

        dispatcherService.dispatcherUserReport();
        verify(producer).sendMessage(message);
        verify(aggregationService).buildUserTasksReport();
    }
}
