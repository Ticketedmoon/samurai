package com.skybreak.samurai.infrastructure.service.consumer;

import com.skybreak.samurai.infrastructure.domain.model.GameChangeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GameKafkaConsumer {

    @KafkaListener(
            clientIdPrefix = "samurai",
            groupId = "${kafka.consumer.groupId}",
            topics = "${kafka.topic.name}",
            containerFactory = "kafkaListenerContainerFactory",
            concurrency = "3")
    public void handleGameChangeEvent(
            @Payload GameChangeEvent message,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
        log.info("Received message: {}, from partition: {}", message, partition);
    }

}
