package com.zhvavyy.sender.messaging.consumer;

import com.zhvavyy.sender.dto.MessageForEmail;
import com.zhvavyy.sender.service.EmailSenderImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegisterConsumer {

   private final EmailSenderImpl emailSender;

    @KafkaListener(topics = "${app.kafka.topics.user-registration}",groupId = "my_cons")
    public void listen(MessageForEmail data){
        emailSender.sendMail(data);
    }
}
