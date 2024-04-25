package com.skybreak.samurai.application.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.skybreak.samurai.application.domain.model.SearchParams;
import com.skybreak.samurai.application.domain.model.SearchResponse;
import com.skybreak.samurai.application.service.SearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

// This is just a test controller for the moment
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1")
public class SearchController {

    private final SearchService searchService;

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
    public ResponseEntity<?> test() throws URISyntaxException, JsonProcessingException {
        SearchResponse responseA = searchService.lookupSearchWithRetrieve().block();

        SearchResponse responseB = searchService.lookupSearchWithExchange().block();

        SearchParams params = SearchParams.builder()
            .username("Ticketedmoon")
            .age(27)
            .build();

        String responseBodyAsString = searchService.postWithExchange(params);

        return ResponseEntity.ok(responseBodyAsString);
    }

    @PostMapping(path = "/create")
    public ResponseEntity<?> create(@RequestBody @Valid SearchParams searchParams) {
        log.info(searchParams.toString());
        return ResponseEntity.created(URI.create("/api/v1/create")).body(searchParams);
    }

}
