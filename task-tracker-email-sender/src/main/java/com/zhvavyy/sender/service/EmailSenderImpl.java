package com.zhvavyy.sender.service;

import com.zhvavyy.sender.dto.DataForSendEmail;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailParseException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSenderImpl implements EmailSender {

    @Value("${spring.mail.username}")
    String sender;
    private final JavaMailSender mailSender;

    @Override
    public String sendMail(DataForSendEmail data) {
       try {
           SimpleMailMessage message = new SimpleMailMessage();
           message.setFrom(sender);
           message.setTo(data.getRecipient());
           message.setSubject(data.getSubject());
           message.setText(data.getMsgBody());

           mailSender.send(message);
           return "Mail sent successfully.";

       }catch (Exception ex){
           throw new MailParseException(ex.getMessage());
       }
    }
}
