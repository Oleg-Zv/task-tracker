package com.zhvavyy.scheduler.unit.kafka;

import com.zhvavyy.scheduler.kafka.messaging.dto.MessageForEmail;
import com.zhvavyy.scheduler.kafka.messaging.producer.UserReportProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserReportProducerTest {

    @Mock
    private KafkaTemplate<String, MessageForEmail>kafkaTemplate;
    @Test
    public void sendMessage(){
        UserReportProducer userReportProducer = new UserReportProducer(kafkaTemplate);
        ReflectionTestUtils.setField(userReportProducer, "userReport", "topic");
        MessageForEmail message = MessageForEmail.builder()
                .recipient("test")
                .msgBody("test")
                .subject("test")
                .build();
        userReportProducer.sendMessage(message);
        verify(kafkaTemplate).send(anyString(), eq(message));
    }
}
