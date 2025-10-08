package com.zhvavyy.sender.service;

import com.zhvavyy.sender.dto.MessageForEmail;

public interface EmailSender {
    String sendMail(MessageForEmail data);
}
