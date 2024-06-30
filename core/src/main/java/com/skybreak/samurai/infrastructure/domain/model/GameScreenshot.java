package com.skybreak.samurai.infrastructure.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

@Entity
@Getter
@Setter
@Builder
@Jacksonized
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class GameScreenshot {

    @Id
    private final long id;
    @Column(length = 1024)
    private final String url;
}
