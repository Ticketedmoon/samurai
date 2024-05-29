package com.skybreak.samurai.application.domain.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "metadata-aggregation-service.api.client")
public class ApiClientProperties {

    private String id;

    private String secret;
}
