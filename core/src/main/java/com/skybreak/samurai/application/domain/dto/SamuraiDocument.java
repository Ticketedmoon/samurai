package com.skybreak.samurai.application.domain.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.skybreak.samurai.infrastructure.domain.model.GameScreenshot;
import java.math.BigDecimal;
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
public class SamuraiDocument {

    private long id;
    private String name;
    private String summary;
    private BigDecimal samuraiRating;
    private List<GameScreenshot> screenshots;
}
