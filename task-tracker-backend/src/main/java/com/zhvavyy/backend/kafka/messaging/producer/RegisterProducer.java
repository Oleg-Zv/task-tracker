package com.zhvavyy.backend.kafka.messaging.producer;

import com.zhvavyy.backend.kafka.messaging.dto.DataForSendEmail;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RegisterProducer {

    @Value("${app.kafka.topics.user-registration}")
    private String userRegister;
    private final KafkaTemplate<String,DataForSendEmail> kafkaTemplate;

    public void sendMessage(DataForSendEmail data){
        kafkaTemplate.send(userRegister,data);
    }
}
