package com.skybreak.samurai.application.service;

import com.skybreak.samurai.application.domain.dto.SamuraiDocument;
import com.skybreak.samurai.infrastructure.domain.model.GameChangeEvent;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameIndexingOrchestrator {

    private final IndexingService indexingService;

    public void performChangeEventIndexing(GameChangeEvent event) throws IOException {
        BigDecimal rating = calculateRating(event);
        SamuraiDocument document = SamuraiDocument.builder()
                .id(event.getKey())
                .name(event.getName())
                .summary(event.getSummary())
                .samuraiRating(rating)
                .screenshots(event.getScreenshots())
                .build();

        indexingService.performIndex(document);
    }

    private static BigDecimal calculateRating(GameChangeEvent event) {
        return BigDecimal.valueOf(event.getRating())
                .add(BigDecimal.valueOf(event.getAggregatedRating()))
                .divide(BigDecimal.TWO, RoundingMode.HALF_EVEN)
                .setScale(3, RoundingMode.HALF_EVEN);
    }

}
