package com.skybreak.samurai.application.service;

import com.api.igdb.exceptions.RequestException;
import com.api.igdb.request.IGDBWrapper;
import com.api.igdb.request.TwitchAuthenticator;
import com.api.igdb.utils.Endpoints;
import com.api.igdb.utils.TwitchToken;
import com.google.protobuf.InvalidProtocolBufferException;
import com.skybreak.samurai.application.domain.properties.ApiClientProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import proto.Game;
import proto.GameResult;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameCatalogueCollectionService {

    private static final String NEXT_GAME_LIST_API_QUERY_TEMPLATE = """
        fields: id, name, rating, aggregated_rating;
        sort aggregated_rating desc;
        offset %s;
        limit 100;
        """;

    private final ApiClientProperties apiClientProperties;

    @PostConstruct
    void scanGameCatalogueForEvents() throws InvalidProtocolBufferException, RequestException {
        TwitchToken twitchToken = acquireAccessToken();

        IGDBWrapper wrapper = IGDBWrapper.INSTANCE;
        wrapper.setCredentials(apiClientProperties.getId(), twitchToken.getAccess_token());

        List<Game> gameSubList = searchGames(wrapper, 0);

        // push to kafka topic
    }

    private List<Game> searchGames(IGDBWrapper wrapper, long positionOffset) throws RequestException, InvalidProtocolBufferException {
        String query = NEXT_GAME_LIST_API_QUERY_TEMPLATE.formatted(positionOffset);
        byte[] bytes = wrapper.apiProtoRequest(Endpoints.GAMES, query);
        return GameResult.parseFrom(bytes).getGamesList();
    }

    private TwitchToken acquireAccessToken() {
        TwitchAuthenticator tAuth = TwitchAuthenticator.INSTANCE;
        return tAuth.requestTwitchToken(apiClientProperties.getId(), apiClientProperties.getSecret());
    }
}
