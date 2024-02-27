package kafka.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import kafka.shared.Message;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Runner {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ConsumerFactory<String, String> consumerFactory;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(
            topics = "test",
            groupId = "worker",
            properties = "auto.offset.reset:earliest"
    )
    @SneakyThrows
    public void consume(ConsumerRecord<String, String> record, String content) {
        log.info("Consuming from partition {} at offset {}", record.partition(), record.offset());
        var message = objectMapper.readValue(content, Message.class);
        log.info(">> {}", message);
        Thread.sleep(100);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void run() {
        kafkaTemplate.setConsumerFactory(consumerFactory);
        try {
            var record = kafkaTemplate.receive("test", 0, 500);
            log.info("Received by specific offset: {}", record.value());
        } catch (Exception e) {
            log.warn("Unable to read from specific partition & offset");
        }
    }
}
