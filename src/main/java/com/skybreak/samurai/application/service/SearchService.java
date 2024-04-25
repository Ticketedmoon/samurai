package com.skybreak.samurai.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skybreak.samurai.application.domain.model.SearchParams;
import com.skybreak.samurai.application.domain.model.SearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    public final ObjectMapper objectMapper;

    private final WebClient webClient;

    public Mono<SearchResponse> lookupSearchWithRetrieve() {
        WebClient.ResponseSpec responseSpec = webClient.get()
            .uri(builder -> builder
                .scheme("http")
                .host("localhost")
                .port(8080)
                .path("/api/v1/search")
                .build())
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
            .acceptCharset(StandardCharsets.UTF_8)
            .retrieve();
        return responseSpec.bodyToMono(SearchResponse.class);
    }

    public Mono<SearchResponse> lookupSearchWithExchange() {
        WebClient.RequestHeadersSpec<?> requestHeadersSpec = webClient.get()
            .uri(builder -> builder
                .scheme("http")
                .host("localhost")
                .port(8080)
                .path("/api/v1/search")
                .build())
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
            .acceptCharset(StandardCharsets.UTF_8);

        return requestHeadersSpec.exchangeToMono(response -> {
            if (response.statusCode().equals(HttpStatus.OK)) {
                return response.bodyToMono(SearchResponse.class);
            } else if (response.statusCode().is4xxClientError()) {
                return Mono.empty();
            } else {
                return response.createException().flatMap(Mono::error);
            }
        });
    }

    public String postWithExchange(SearchParams searchParams) throws URISyntaxException, JsonProcessingException {
        WebClient.RequestHeadersSpec<?> requestHeadersSpec = webClient.post()
            .uri(new URI("http://localhost:8080/api/v1/create"))
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "N/A")
            .acceptCharset(StandardCharsets.UTF_8)
            .bodyValue(searchParams);

        Object responseBody = requestHeadersSpec
            .exchangeToMono(response -> {
                if (response.statusCode().is2xxSuccessful()) {
                    return response.bodyToMono(SearchParams.class);
                } else if (response.statusCode().is4xxClientError()) {
                    return Mono.just("Error response");
                } else {
                    return response.createException().flatMap(Mono::error);
                }
            })
            .block();

        return objectMapper.writeValueAsString(responseBody);
    }


}
