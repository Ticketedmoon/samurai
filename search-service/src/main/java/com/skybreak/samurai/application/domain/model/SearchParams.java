package com.skybreak.samurai.application.domain.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class SearchParams {

    @NotBlank
    String q;

    @NotBlank
    String language;
}
