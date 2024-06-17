package com.skybreak.samurai.application.controller;

import com.skybreak.samurai.application.domain.dto.SamuraiDocument;
import com.skybreak.samurai.application.domain.model.SearchParams;
import com.skybreak.samurai.application.service.SearchService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1")
public class SearchController {

    private final SearchService searchService;

    @PostMapping(path = "/search")
    public ResponseEntity<List<SamuraiDocument>> search(@RequestBody @Valid SearchParams searchParams)
            throws IOException {
        List<SamuraiDocument> documents = searchService.performSearch(searchParams);
        return documents.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(documents);
    }

}
