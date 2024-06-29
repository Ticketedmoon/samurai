package com.skybreak.samurai.application.controller.exception;

import org.opensearch.client.opensearch._types.OpenSearchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GeneralExceptionHandler {

    @ExceptionHandler(value = ResponseStatusException.class)
    private ResponseStatusException handleConflict(ResponseStatusException ex) {
        return ex;
    }

    @ExceptionHandler(value = OpenSearchException.class)
    private ResponseEntity<ProblemDetail> handleConflict(OpenSearchException ex) {

        ProblemDetail problemDetail = ProblemDetail
                .forStatusAndDetail(HttpStatusCode.valueOf(ex.status()), ex.getMessage());

        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        return ResponseEntity
                .of(problemDetail)
                .headers(headers)
                .build();
    }
}
