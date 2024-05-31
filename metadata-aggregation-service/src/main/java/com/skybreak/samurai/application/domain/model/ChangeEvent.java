package com.skybreak.samurai.application.domain.model;


import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.Map;

@Getter
@ToString
@Jacksonized
@SuperBuilder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ChangeEvent {

    @JsonAlias("id")
    private long key;
    private Operation op;
    private String messageUuid;
    private String timestampString;
    private Map<String, Object> header;
}
