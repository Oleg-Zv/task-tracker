package com.zhvavyy.sender.unit.service;

import com.zhvavyy.sender.dto.MessageForEmail;
import com.zhvavyy.sender.service.EmailSenderImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailParseException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmailSenderImplTest {


    @Mock
    private JavaMailSender javaMailSender;
    private MessageForEmail messageForEmail;

    @BeforeEach
    public void init(){
        messageForEmail = new MessageForEmail();
        messageForEmail.setRecipient("test@gmail.com");
        messageForEmail.setMsgBody("test");
        messageForEmail.setSubject("test");
    }

    @Test
    public void sendEmail(){
        EmailSenderImpl emailSender = new EmailSenderImpl(javaMailSender);
        ReflectionTestUtils.setField(emailSender, "sender", "from");
        String string = emailSender.sendMail(messageForEmail);
        assertFalse(string.isEmpty());

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender).send(captor.capture());

        SimpleMailMessage sentMessage = captor.getValue();
        assertEquals("from", sentMessage.getFrom());
        assertNotNull(sentMessage.getTo());
        assertEquals("test@gmail.com", sentMessage.getTo()[0]);
        assertEquals("test", sentMessage.getSubject());
        assertEquals("test", sentMessage.getText());
    }

    @Test
    public void sendEmail_shouldThrowExc(){
        EmailSenderImpl emailSender = new EmailSenderImpl(javaMailSender);
        when(emailSender.sendMail(messageForEmail)).thenThrow(MailParseException.class);
       assertThrows(MailParseException.class, ()->emailSender.sendMail(messageForEmail));
    }
}
