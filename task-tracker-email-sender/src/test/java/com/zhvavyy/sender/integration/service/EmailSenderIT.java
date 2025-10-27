package com.zhvavyy.sender.integration.service;


import com.zhvavyy.sender.dto.MessageForEmail;
import com.zhvavyy.sender.integration.BaseIntegrationTest;
import com.zhvavyy.sender.service.EmailSenderImpl;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;


import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmailSenderIT extends BaseIntegrationTest {

    @Value("${spring.mail.from}")
    String sender;

    @Autowired
    private EmailSenderImpl emailSender;
    private static final String TEST_EMAIL = "user@gmail.com";
    private static final String SUCCESS_SMS = "Mail sent successfully.";

    @Test
    public void sendMail() {
        MessageForEmail message = new MessageForEmail();
        message.setRecipient(TEST_EMAIL);
        message.setMsgBody("test");
        message.setSubject("test");


        String result = emailSender.sendMail(message);

        assertEquals(SUCCESS_SMS, result);

        MimeMessage[] receivedMessages = BaseIntegrationTest.greenMail.getReceivedMessages();

        assertAll(
                () -> assertEquals(1, receivedMessages.length),
                () -> assertEquals(TEST_EMAIL, receivedMessages[0].getAllRecipients()[0].toString()),
                () -> assertEquals("test", receivedMessages[0].getSubject()),
                () -> assertEquals("test", receivedMessages[0].getContent())
        );
    }
}
