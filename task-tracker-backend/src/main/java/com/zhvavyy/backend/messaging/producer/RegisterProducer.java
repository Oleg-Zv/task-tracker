package com.zhvavyy.backend.messaging.producer;

import com.zhvavyy.backend.messaging.producer.dto.DataForSendEmail;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RegisterProducer {

    private final KafkaTemplate<String,DataForSendEmail> kafkaTemplate;

    public void sendMessage(DataForSendEmail data){
kafkaTemplate.send("EMAIL_SENDING_TASKS",data);
    }
}
