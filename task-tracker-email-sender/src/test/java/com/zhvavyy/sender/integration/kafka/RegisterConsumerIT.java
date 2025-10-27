package com.zhvavyy.sender.integration.kafka;


import com.zhvavyy.sender.dto.MessageForEmail;
import com.zhvavyy.sender.integration.BaseIntegrationTest;
import com.zhvavyy.sender.messaging.consumer.RegisterConsumer;
import com.zhvavyy.sender.service.EmailSenderImpl;
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

import static org.junit.jupiter.api.Assertions.*;

public class RegisterConsumerIT extends BaseIntegrationTest {

    private static final String SUCCESS_SMS = "Mail sent successfully.";
    private static final String TEST_EMAIL = "user@gmail.com";


    @Value("${app.kafka.topics.user-registration}")
    private String userRegister;

    @Autowired
    private KafkaTemplate<String, MessageForEmail> kafkaTemplate;

    @Autowired
    private RegisterConsumer registerConsumer;

    @Autowired
    private EmailSenderImpl emailSender;

    @Test
    void listen_success() {
        MessageForEmail message = new MessageForEmail(TEST_EMAIL, "test", "test");

        registerConsumer.listen(message);

        String result = emailSender.sendMail(message);
        assertEquals(SUCCESS_SMS, result);
    }


    @Test
    void testSendMessageToKafka() throws ExecutionException, InterruptedException, TimeoutException {
        MessageForEmail message = new MessageForEmail(TEST_EMAIL, "test", "test");

        CompletableFuture<SendResult<String, MessageForEmail>> future = kafkaTemplate.send(userRegister, message);
        SendResult<String, MessageForEmail> result = future.get(5, TimeUnit.SECONDS);

        assertNotNull(result);
        assertNotNull(result.getRecordMetadata());
        assertEquals(userRegister, result.getRecordMetadata().topic());
        assertEquals(message.getRecipient(), result.getProducerRecord().value().getRecipient());
    }

    @Test
    void testSendEmail() {
        MessageForEmail message = new MessageForEmail(TEST_EMAIL, "test", "test");

        String sendMail = emailSender.sendMail(message);

        assertEquals(SUCCESS_SMS, sendMail);
    }

    @Test
    void testMessageConsumedFromKafka() {
        MessageForEmail message = new MessageForEmail(TEST_EMAIL, "test", "test");

        kafkaTemplate.send(userRegister, message);

        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, getKafkaBootstrapServers());
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group-consumer");
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        consumerProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, MessageForEmail.class.getName());
        consumerProps.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);


        try (KafkaConsumer<String, MessageForEmail> consumer = new KafkaConsumer<>(consumerProps)) {
            consumer.subscribe(List.of(userRegister));

            ConsumerRecord<String, MessageForEmail> record = KafkaTestUtils.getSingleRecord(consumer, userRegister, Duration.ofSeconds(10));

            assertNotNull(record);
            assertEquals(TEST_EMAIL, record.value().getRecipient());
            assertEquals("test", record.value().getMsgBody());
        }
    }
}
