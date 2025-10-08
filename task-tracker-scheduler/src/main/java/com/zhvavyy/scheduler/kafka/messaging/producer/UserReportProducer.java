package com.zhvavyy.scheduler.kafka.messaging.producer;

import com.zhvavyy.scheduler.kafka.messaging.dto.MessageForEmail;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserReportProducer {

    @Value("${app.kafka.topics.email-sending}")
    private String userReport;
    private final KafkaTemplate<String, MessageForEmail> kafkaTemplate;

    public void sendMessage(MessageForEmail data){
        kafkaTemplate.send(userReport,data);
    }
}