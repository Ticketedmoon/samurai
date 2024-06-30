package com.skybreak.samurai.infrastructure.domain.model;


import static jakarta.persistence.CascadeType.ALL;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Entity
@Getter
@ToString
@Jacksonized
@SuperBuilder
@Table(name = "game_change_events")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public final class GameChangeEvent extends ChangeEvent {

    private String name;

    @Column(length = 8192)
    private String summary;

    private double rating;

    @Column(name = "aggregated_rating", nullable = false)
    private double aggregatedRating;

    @OneToMany(targetEntity = GameScreenshot.class, cascade = ALL, fetch = FetchType.LAZY)
    private List<GameScreenshot> screenshots;
}
