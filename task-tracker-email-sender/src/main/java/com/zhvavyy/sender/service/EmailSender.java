package com.zhvavyy.sender.service;

import com.zhvavyy.sender.dto.DataForSendEmail;

public interface EmailSender {
    String sendMail(DataForSendEmail data);
}
