package com.skybreak.samurai.application.controller;

import com.skybreak.samurai.application.domain.model.SearchResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

// This is just a test controller for the moment
@Slf4j
@RestController
@RequestMapping(path = "/api")
public class SearchController {

    @GetMapping(path = "/search")
    public ResponseEntity<SearchResponse> search() {
        SearchResponse searchResponse = SearchResponse.builder()
            .searchTrackingId(UUID.randomUUID().toString())
            .language("en")
            .content(null)
            .build();
        return ResponseEntity.ok(searchResponse);
    }
}
