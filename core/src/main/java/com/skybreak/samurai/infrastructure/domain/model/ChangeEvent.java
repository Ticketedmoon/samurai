package com.skybreak.samurai.infrastructure.domain.model;


import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Getter
@ToString
@Jacksonized
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ChangeEvent {

    @Id
    @JsonAlias("id")
    private long key;

    private Operation op;

    @Column(name = "message_uuid", nullable = false, length = 512)
    private String messageUuid;

    @Column(name = "timestamp_string", nullable = false, length = 512)
    private String timestampString;

    // Do we want to persist this info? TODO Look into way to add
    @Transient
    private Map<String, Object> header;
}
