package com.zhvavyy.sender.unit.kafka;

import com.zhvavyy.sender.dto.MessageForEmail;
import com.zhvavyy.sender.messaging.consumer.UserReportConsumer;
import com.zhvavyy.sender.service.EmailSenderImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserReportConsumerTest {

    @InjectMocks
    private UserReportConsumer userReportConsumer;
    @Mock
    private EmailSenderImpl emailSender;

    @Test
    public void listen(){
        MessageForEmail messageForEmail = new MessageForEmail();
        messageForEmail.setRecipient("test@gmail.com");
        messageForEmail.setMsgBody("test");
        messageForEmail.setSubject("test");

        userReportConsumer.listen(messageForEmail);
        verify(emailSender).sendMail(messageForEmail);
    }
}
