package com.skybreak.samurai.infrastructure.domain.model;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
public class GameScreenshot {

    private final long id;
    private final String url;
}
