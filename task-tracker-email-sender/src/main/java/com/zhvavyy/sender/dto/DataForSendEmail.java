package com.zhvavyy.sender.dto;
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