package com.skybreak.samurai.application.service;

import com.skybreak.samurai.application.domain.dto.SamuraiDocument;
import com.skybreak.samurai.infrastructure.domain.model.GameChangeEvent;
import com.skybreak.samurai.infrastructure.domain.model.GameScreenshot;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameIndexingOrchestrator {

    private static final String IGDB_HIGHEST_RESOLUTION_TYPE = "t_1080p";

    private final IndexingService indexingService;

    public void performChangeEventIndexing(GameChangeEvent event) throws IOException {
        BigDecimal rating = calculateRating(event);
        List<GameScreenshot> screenshots = buildHighResolutionScreenshot(event);
        SamuraiDocument document = SamuraiDocument.builder()
                .id(event.getKey())
                .name(event.getName())
                .summary(event.getSummary())
                .samuraiRating(rating)
                .screenshots(screenshots)
                .build();

        indexingService.performIndex(document);
    }

    private static List<GameScreenshot> buildHighResolutionScreenshot(GameChangeEvent event) {
        return event.getScreenshots()
                .stream()
                .map(gameScreenshot -> {
                    String[] groups = gameScreenshot.getUrl().split("/");
                    groups[groups.length - 2] = IGDB_HIGHEST_RESOLUTION_TYPE;
                    String newUrl = String.join("/", groups);
                    return GameScreenshot.builder()
                            .id(gameScreenshot.getId())
                            .url(newUrl)
                            .build();
                })
                .toList();
    }

    private static BigDecimal calculateRating(GameChangeEvent event) {
        return BigDecimal.valueOf(event.getRating())
                .add(BigDecimal.valueOf(event.getAggregatedRating()))
                .divide(BigDecimal.TWO, RoundingMode.HALF_EVEN)
                .setScale(3, RoundingMode.HALF_EVEN);
    }

}
