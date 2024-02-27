package kafka.shared;

import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class Message {
    private final UUID id;
    private final String user;
    private final Map<String, Object> properties;
}
