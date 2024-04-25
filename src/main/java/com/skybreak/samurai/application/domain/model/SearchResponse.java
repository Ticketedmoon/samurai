package com.skybreak.samurai.application.domain.model;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class SearchResponse {

    String searchTrackingId;

    String language;

    Object content;
}
