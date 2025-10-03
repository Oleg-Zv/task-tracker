package com.zhvavyy.backend.messaging.producer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataForSendEmail {
    private String recipient;
    private String msgBody;
    private String subject;
}