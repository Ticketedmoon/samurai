package com.skybreak.samurai.infrastructure.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Setter
@Builder
@Jacksonized
public class GameScreenshot {

    private final long id;
    private final String url;
}
