package com.zhvavyy.sender.messaging.consumer;

import com.zhvavyy.sender.dto.DataForSendEmail;
import com.zhvavyy.sender.service.EmailSenderImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;

@RequiredArgsConstructor
public class EmailConsumer {

   private final EmailSenderImpl emailSender;

    @KafkaListener(topics = "EMAIL_SENDING_TASKS",groupId = "my_cons")
    public void listen(DataForSendEmail data){
        emailSender.sendMail(data);
    }
}
