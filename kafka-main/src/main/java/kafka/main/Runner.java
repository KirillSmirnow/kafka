package kafka.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import kafka.shared.Message;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

import static java.util.Map.entry;
import static java.util.UUID.randomUUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class Runner {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @EventListener(ApplicationReadyEvent.class)
    @SneakyThrows
    public void run() {
        while (true) {
            Thread.sleep(1000);
            var message = generateMessage();
            var sendResult = kafkaTemplate.send(
                    "test",
                    message.getId().toString(),
                    objectMapper.writeValueAsString(message)
            ).join();
            var metadata = sendResult.getRecordMetadata();
            log.info("Message sent to partition {} with offset {}", metadata.partition(), metadata.offset());
        }
    }

    private Message generateMessage() {
        return Message.builder()
                .id(randomUUID())
                .user("kirill")
                .properties(Map.ofEntries(
                        entry("a", 123.7),
                        entry("k", 4),
                        entry("ts", LocalDateTime.now().toString())
                ))
                .build();
    }
}
