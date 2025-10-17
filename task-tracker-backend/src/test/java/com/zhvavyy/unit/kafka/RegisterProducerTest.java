package com.zhvavyy.unit.kafka;

import com.zhvavyy.backend.kafka.messaging.dto.MessageForEmail;
import com.zhvavyy.backend.kafka.messaging.producer.RegisterProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegisterProducerTest {

    @Mock
    private KafkaTemplate<String, MessageForEmail> kafkaTemplate;

    @Test
    public void sendMessage(){
        RegisterProducer registerProducer = new RegisterProducer(kafkaTemplate);
        ReflectionTestUtils.setField(registerProducer, "userRegister", "topic");
        MessageForEmail message = MessageForEmail.builder()
                .recipient("test")
                .msgBody("test")
                .subject("test")
                .build();
        registerProducer.sendMessage(message);
        verify(kafkaTemplate).send(anyString(), eq(message));
    }
}
