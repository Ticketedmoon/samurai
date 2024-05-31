package com.skybreak.samurai.infrastructure.config;

import java.util.Map;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class TopicConfiguration {

    @Bean
    public KafkaAdmin createKafkaAdmin(
            @Value(value = "${spring.kafka.bootstrap-servers}") String bootstrapAddress) {
        Map<String, Object> configs = Map.of(
            AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress
        );
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic createGameMetadataTopic(
            @Value(value = "${kafka.topic.name}") String topicName) {
        return TopicBuilder.name(topicName)
                .partitions(3)
            .replicas(1)
            .build();
    }
}
