package com.skybreak.samurai.application.domain.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.validator.constraints.Range;

@Getter
@Builder
@Jacksonized
@EqualsAndHashCode
public class SearchParams {

    @Setter
    @NotNull
    private String q;

    @NotBlank
    private final String language;

    @Range(min = 10, max = 100)
    private final Integer rows = 100;
}
