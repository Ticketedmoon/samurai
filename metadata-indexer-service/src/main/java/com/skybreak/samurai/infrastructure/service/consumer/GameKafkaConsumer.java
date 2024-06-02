package com.skybreak.samurai.infrastructure.service.consumer;

import com.skybreak.samurai.application.service.GameIndexingOrchestrator;
import com.skybreak.samurai.infrastructure.domain.model.GameChangeEvent;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GameKafkaConsumer {

    private final GameIndexingOrchestrator gameIndexingOrchestrator;

    @KafkaListener(
            clientIdPrefix = "samurai",
            groupId = "${metadata-indexer-service.kafka.consumer.groupId}",
            topics = "${metadata-indexer-service.kafka.topic.name}",
            containerFactory = "kafkaListenerContainerFactory",
            concurrency = "3")
    public void handleGameChangeEvent(
            @Payload GameChangeEvent changeEvent,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) throws IOException {
        log.debug("Received message: {}, from partition: {}", changeEvent, partition);
        gameIndexingOrchestrator.performChangeEventIndexing(changeEvent);
    }

}
