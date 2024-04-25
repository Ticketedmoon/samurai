package com.skybreak.samurai.application.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skybreak.samurai.application.domain.model.SearchParams;
import com.skybreak.samurai.application.domain.model.SearchResponse;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

// This is just a test controller for the moment
@Slf4j
@Validated
@RestController
@RequestMapping(path = "/api/v1")
public class SearchController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping(path = "/search")
    public ResponseEntity<SearchResponse> search() {
        SearchResponse searchResponse = SearchResponse.builder()
            .searchTrackingId(UUID.randomUUID().toString())
            .language("en")
            .content(null)
            .build();
        return ResponseEntity.ok(searchResponse);
    }

    @GetMapping(path = "/test")
    public ResponseEntity<?> test() throws JsonProcessingException, URISyntaxException {
        WebClient webClient = createWebClient();

        SearchResponse responseA = lookupSearchWithRetrieve(webClient).block();

        SearchResponse responseB = lookupSearchWithExchange(webClient).block();

        SearchParams params = SearchParams.builder()
            .username("Ticketedmoon")
            .age(27)
            .build();

        Object responseBody = postWithExchange(webClient, params).block();
        return ResponseEntity.ok(objectMapper.writeValueAsString(responseBody));
    }

    @PostMapping(path = "/create")
    public ResponseEntity<?> create(@RequestBody @Valid SearchParams searchParams) {
        log.info(searchParams.toString());
        return ResponseEntity.created(URI.create("/api/v1/create")).body(searchParams);
    }

    private static Mono<SearchResponse> lookupSearchWithRetrieve(WebClient webClient) {
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

    private static Mono<SearchResponse> lookupSearchWithExchange(WebClient webClient) {
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

    private static Mono<?> postWithExchange(WebClient webClient, SearchParams searchParams) throws URISyntaxException {
        WebClient.RequestHeadersSpec<?> requestHeadersSpec = webClient.post()
            .uri(new URI("http://localhost:8080/api/v1/create"))
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "N/A")
            .acceptCharset(StandardCharsets.UTF_8)
            .bodyValue(searchParams);

        return requestHeadersSpec.exchangeToMono(response -> {
            if (response.statusCode().is2xxSuccessful()) {
                return response.bodyToMono(SearchParams.class);
            } else if (response.statusCode().is4xxClientError()) {
                return Mono.just("Error response");
            } else {
                return response.createException().flatMap(Mono::error);
            }
        });
    }

    private static WebClient createWebClient() {
        HttpClient httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
            .responseTimeout(Duration.ofMillis(10000))
            .doOnConnected(conn ->
                conn.addHandlerLast(new ReadTimeoutHandler(10000, TimeUnit.MILLISECONDS))
                    .addHandlerLast(new WriteTimeoutHandler(10000, TimeUnit.MILLISECONDS)));

        return WebClient.builder()
            .defaultCookie("cookieKey", "N/A")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .build();
    }
}
