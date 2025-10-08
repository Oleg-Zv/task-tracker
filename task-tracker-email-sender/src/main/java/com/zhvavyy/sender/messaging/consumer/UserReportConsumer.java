package com.zhvavyy.sender.messaging.consumer;

import com.zhvavyy.sender.dto.MessageForEmail;
import com.zhvavyy.sender.service.EmailSenderImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserReportConsumer {
    private final EmailSenderImpl emailSender;

    @KafkaListener(topics = "${app.kafka.topics.email-sending}",groupId = "my_cons2")
    public void listen(MessageForEmail data){
        emailSender.sendMail(data);
    }

}
