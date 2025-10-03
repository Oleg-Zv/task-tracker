package com.zhvavyy.backend.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic topic(){
        return new NewTopic(
                "EMAIL_SENDING_TASKS",
                3,
                (short) 1);
    }
}
