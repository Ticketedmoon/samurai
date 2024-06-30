package com.skybreak.samurai.application.service;

import com.api.igdb.exceptions.RequestException;
import com.api.igdb.request.IGDBWrapper;
import com.api.igdb.request.TwitchAuthenticator;
import com.api.igdb.utils.Endpoints;
import com.api.igdb.utils.TwitchToken;
import com.google.protobuf.InvalidProtocolBufferException;
import com.skybreak.samurai.application.domain.properties.ApiClientProperties;
import com.skybreak.samurai.application.persistence.repository.GameChangeEventRepository;
import com.skybreak.samurai.infrastructure.domain.model.GameChangeEvent;
import com.skybreak.samurai.infrastructure.domain.model.GameScreenshot;
import com.skybreak.samurai.infrastructure.domain.model.Operation;
import com.skybreak.samurai.infrastructure.service.producer.GameKafkaProducer;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import proto.Game;
import proto.GameResult;

@Service
@RequiredArgsConstructor
public class GameCollectionService {

    private static final int TOTAL_GAMES_TO_CONSUME_PER_PAGE = 200;
    private static final String NEXT_GAME_LIST_API_QUERY_TEMPLATE = """
            fields: id, name, summary, rating, aggregated_rating, screenshots.url;
            sort aggregated_rating desc;
            offset %s;
            limit %s;
            """;

    private final ApiClientProperties apiClientProperties;

    private final GameKafkaProducer gameKafkaProducer;
    private final GameChangeEventRepository changeEventRepository;

    // When changing this to @Scheduled to poll documents with a certain periodicity, we do not want to lose the
    // position or cursor of the catalogue scan. Therefore, in fear of instance or pod restarts, we want to persist the
    // scan position periodically, so we never need to restart the scan fully. We may lose a small bit of progress but
    // that is trivial, and a trade-off that I am fine to allow.
    // To achieve this, we can make use of our source-of-truth DB to know which rows have been scanned, as each record
    // will store which cursor/positional block it was derived from.
    // Given an instance restart, we simply will just need to check the latest block scanned from the db and continue
    // from that point.
    @PostConstruct
    void scanGameCatalogueForEvents() throws InvalidProtocolBufferException, RequestException {
        TwitchToken twitchToken = acquireAccessToken();

        IGDBWrapper wrapper = IGDBWrapper.INSTANCE;
        wrapper.setCredentials(apiClientProperties.getId(), twitchToken.getAccess_token());

        List<Game> gameSubList = searchGames(wrapper, 0);

        List<GameChangeEvent> gameChangeEvents = buildChangeEventsForGameSubList(gameSubList);

        // Write to DB (Source of Truth)
        changeEventRepository.saveAll(gameChangeEvents);

        gameKafkaProducer.publishEvents(gameChangeEvents);
    }

    private static List<GameChangeEvent> buildChangeEventsForGameSubList(List<Game> gameSubList) {
        return gameSubList.stream()
                .map(changeEvent -> {
                    List<GameScreenshot> gameChangeEventScreenshots = buildGameChangeEventScreenshots(changeEvent);

                    return GameChangeEvent.builder()
                            .key(changeEvent.getId())
                            .op(Operation.CREATE)
                            .messageUuid(UUID.randomUUID().toString())
                            .timestampString(LocalDateTime.now().atOffset(ZoneOffset.UTC).toString())
                            .name(changeEvent.getName())
                            .summary(changeEvent.getSummary())
                            .rating(changeEvent.getRating())
                            .aggregatedRating(changeEvent.getAggregatedRating())
                            .screenshots(gameChangeEventScreenshots)
                            .build();
                })
                .collect(Collectors.toUnmodifiableList());
    }

    private static List<GameScreenshot> buildGameChangeEventScreenshots(Game changeEvent) {
        return changeEvent.getScreenshotsList().stream()
                .map(screenshot -> GameScreenshot.builder()
                        .id(screenshot.getId())
                        .url(screenshot.getUrl())
                        .build())
                .toList();
    }

    private List<Game> searchGames(IGDBWrapper wrapper, long positionOffset)
            throws RequestException, InvalidProtocolBufferException {
        String query = NEXT_GAME_LIST_API_QUERY_TEMPLATE.formatted(positionOffset, TOTAL_GAMES_TO_CONSUME_PER_PAGE);
        byte[] bytes = wrapper.apiProtoRequest(Endpoints.GAMES, query);
        return GameResult.parseFrom(bytes).getGamesList();
    }

    private TwitchToken acquireAccessToken() {
        TwitchAuthenticator tAuth = TwitchAuthenticator.INSTANCE;
        return tAuth.requestTwitchToken(apiClientProperties.getId(), apiClientProperties.getSecret());
    }
}
