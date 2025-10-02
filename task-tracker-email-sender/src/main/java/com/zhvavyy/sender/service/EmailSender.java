package com.zhvavyy.sender.service;

import com.zhvavyy.sender.dto.DataForSendEmail;
import lombok.RequiredArgsConstructor;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSender {

    private final JavaMailSender mailSender;

    String from = "argumentoleg@gmail.com";
    String to = "argumentoleg@gmail.com";

    public void send(DataForSendEmail data) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("This is a test email");
        message.setText("Hi - i'm is Oleg, and you?)");

        mailSender.send(message);
    }

}
