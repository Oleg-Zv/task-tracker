package com.zhvavyy.backend.integration.kafka;

import com.zhvavyy.backend.integration.BaseIntegrationTest;
import com.zhvavyy.backend.kafka.messaging.dto.MessageForEmail;
import com.zhvavyy.backend.kafka.messaging.producer.RegisterProducer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.utils.KafkaTestUtils;



import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class RegisterProducerIT extends BaseIntegrationTest {

    @Value("${app.kafka.topics.user-registration}")
    private String userRegister;

    @Autowired
    private RegisterProducer registerProducer;

    @Autowired
    private KafkaTemplate<String, MessageForEmail> kafkaTemplate;

    @Test
    void testKafkaMessage_Success() throws InterruptedException {
        MessageForEmail message = new MessageForEmail(
                "user@gmail.com",
                "test",
                "test");
        registerProducer.sendMessage(message);
        Thread.sleep(3000);
    }

    @Test
    void testSendMessage_WithKafkaTemplate() throws ExecutionException, InterruptedException, TimeoutException {
        MessageForEmail message =
                new MessageForEmail("user@gmail.com", "test", "test");
        CompletableFuture<SendResult<String, MessageForEmail>> future = kafkaTemplate.send(userRegister, message);
        SendResult<String, MessageForEmail> result = future.get(5, TimeUnit.SECONDS);

        assertNotNull(result);
        assertNotNull(result.getRecordMetadata());
        assertEquals(userRegister, result.getRecordMetadata().topic());
        assertEquals(message.getRecipient(), result.getProducerRecord().value().getRecipient());

    }

    @Test
    void testMessageConsumedFromKafka() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, getKafkaBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group-consumer");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        try (KafkaConsumer<String, MessageForEmail> consumer = new KafkaConsumer<>(props)) {
            MessageForEmail message = new MessageForEmail("user@gmail.com", "test", "test");

            consumer.subscribe(List.of(userRegister));
            registerProducer.sendMessage(message);

            ConsumerRecord<String, MessageForEmail> record =
                    KafkaTestUtils.getSingleRecord(consumer, userRegister, Duration.ofSeconds(5));

            assertNotNull(record);
            assertEquals("user@gmail.com", record.value().getRecipient());
        }
    }
}
