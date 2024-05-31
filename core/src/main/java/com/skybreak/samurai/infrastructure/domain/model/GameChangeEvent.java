package com.skybreak.samurai.infrastructure.domain.model;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

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
