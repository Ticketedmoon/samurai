package com.skybreak.samurai.infrastructure.config;

import com.skybreak.samurai.infrastructure.domain.model.ChangeEvent;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@EnableKafka
@Configuration
public class KafkaConsumerConfiguration {

    // TODO: read https://www.confluent.io/blog/microservices-apache-kafka-domain-driven-design/
    // https://www.linkedin.com/pulse/domain-driven-design-understanding-modular-layers-joel-okoromi/

    @Bean
    public ConsumerFactory<String, ChangeEvent> consumerFactory(
            @Value(value = "${spring.kafka.bootstrap-servers}") String bootstrapAddress,
            @Value(value = "${kafka.consumer.groupId}") String groupId) {
        Map<String, Object> props = new HashMap<>();
        // TODO Move me to properties
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        // Are these params necessary?
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ChangeEvent> kafkaListenerContainerFactory(
            ConsumerFactory<String, ChangeEvent> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, ChangeEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        // Discard messages that do not match filter
        //factory.setRecordFilterStrategy(record -> nonNull(record.value()));
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }
}
