package com.skybreak.samurai.application.domain.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.validator.constraints.Range;

@Value
@Builder
@Jacksonized
public class SearchParams {

    @NotBlank
    String q;

    @NotBlank
    String language;

    @Range(min = 10, max = 100)
    Integer rows = 100;
}
