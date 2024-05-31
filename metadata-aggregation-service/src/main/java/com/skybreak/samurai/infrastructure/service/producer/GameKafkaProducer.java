package com.skybreak.samurai.infrastructure.service.producer;

import static java.util.Objects.nonNull;

import com.skybreak.samurai.infrastructure.domain.model.ChangeEvent;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameKafkaProducer {

    @Value(value = "${kafka.topic.name}")
    private String topicName;

    private final KafkaTemplate<String, ChangeEvent> kafkaTemplate;

    public void publishEvents(List<? extends ChangeEvent> gameChangeEvents) {
        gameChangeEvents.forEach(changeEvent ->
                sendPayloadToKafkaTopic(topicName, changeEvent));
    }

    private void sendPayloadToKafkaTopic(final String topic, final ChangeEvent changeEvent) {
        final CompletableFuture<SendResult<String, ChangeEvent>> future = kafkaTemplate.send(topic, changeEvent);
        future.whenComplete((result, ex) -> {
            long eventKey = changeEvent.getKey();
            if (nonNull(ex)) {
                log.error("Unable to send message with key=[%s]".formatted(eventKey), ex);
                return;
            }
            log.info("Sent message with key=[{}] with offset: [{}]", eventKey, result.getRecordMetadata().offset());
        });
    }
}
