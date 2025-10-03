package com.zhvavyy.backend.messaging.producer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DataForSendEmail {
    private String recipient;
    private String msgBody;
    private String subject;
}