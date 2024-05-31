package com.skybreak.samurai.application.domain.model;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Getter
@ToString
@Jacksonized
@SuperBuilder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public final class GameChangeEvent extends ChangeEvent {

    private String name;
    private String summary;
    private double rating;
    private double aggregatedRating;
    private List<GameScreenshot> screenshots;
}
