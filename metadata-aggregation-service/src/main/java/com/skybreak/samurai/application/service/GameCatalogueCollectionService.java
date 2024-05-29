package com.skybreak.samurai.application.service;

import com.skybreak.samurai.application.domain.properties.ApiClientProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class GameCatalogueCollectionService {

    private final ApiClientProperties apiClientProperties;
    private final WebClient webClient;

    @PostConstruct
    void connectToApi() throws URISyntaxException {
        WebClient.ResponseSpec responseSpec = webClient.post()
            .uri(uriBuilder -> uriBuilder
                .scheme("https")
                .host("id.twitch.tv")
                .path("oauth2/token")
                .queryParam("client_id", apiClientProperties.getId())
                .queryParam("client_secret", apiClientProperties.getSecret())
                .queryParam("grant_type", "client_credentials")
                .build()
            )
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
            .acceptCharset(StandardCharsets.UTF_8)
            .retrieve();

        String response = responseSpec.bodyToMono(String.class)
            .onErrorResume(e -> Mono.empty())
            .block();
        assert response != null;
    }
}
