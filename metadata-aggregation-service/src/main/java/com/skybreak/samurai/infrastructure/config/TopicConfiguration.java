package com.skybreak.samurai.infrastructure.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.Map;

@Configuration
public class TopicConfiguration {

    @Bean
    public KafkaAdmin admin() {
        Map<String, Object> configs = Map.of(
            AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092"
        );
        return new KafkaAdmin(configs);
    }

    // Create new topics
    @Bean
    public NewTopic topic1() {
        return TopicBuilder.name("TOPIC-1")
            .partitions(1)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic topic2() {
        return TopicBuilder.name("TOPIC-2")
            .partitions(1)
            .replicas(1)
            .build();
    }

}
