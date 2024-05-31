package com.skybreak.samurai.infrastructure.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skybreak.samurai.application.domain.model.ChangeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameKafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishEvents(List<? extends ChangeEvent> gameChangeEvents) {
        for (ChangeEvent changeEvent : gameChangeEvents) {
            String changeEventPayload = createEventPayloadForChangeEvent(changeEvent);
            if (isNull(changeEventPayload)) {
                continue;
            }

            sendPayloadToKafkaTopic("test_topic", changeEvent.getKey(), changeEventPayload);
        }
    }

    private void sendPayloadToKafkaTopic(final String topic, final long changeEventKey, final String changeEventPayload) {
        final CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, changeEventPayload);
        future.whenComplete((result, ex) -> {
            if (nonNull(ex)) {
                log.error("Unable to send message with key=[%s]".formatted(changeEventKey), ex);
                return;
            }
            log.info("Sent message with key=[{}] with offset: [{}]", changeEventKey, result.getRecordMetadata().offset());
        });
    }

    private String createEventPayloadForChangeEvent(ChangeEvent changeEvent) {
        try {
            return objectMapper.writeValueAsString(changeEvent);
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize change event into json payload: {}", changeEvent, e);
        }
        return null;
    }
}
