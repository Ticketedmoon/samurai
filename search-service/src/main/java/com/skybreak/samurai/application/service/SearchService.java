package com.skybreak.samurai.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skybreak.samurai.application.domain.dto.SamuraiDocument;
import com.skybreak.samurai.application.domain.model.SearchParams;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.query_dsl.FieldValueFactorScoreFunction;
import org.opensearch.client.opensearch._types.query_dsl.FunctionBoostMode;
import org.opensearch.client.opensearch._types.query_dsl.FunctionScore;
import org.opensearch.client.opensearch._types.query_dsl.FunctionScoreMode;
import org.opensearch.client.opensearch._types.query_dsl.FunctionScoreQuery;
import org.opensearch.client.opensearch._types.query_dsl.MatchAllQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.SourceConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    @Value(value = "${metadata-indexer-service.opensearch.write-index.name}")
    private String samuraiIndex;

    public final ObjectMapper objectMapper;

    private final OpenSearchClient openSearchClient;

    public List<SamuraiDocument> performSearch(SearchParams searchParams) throws IOException {
        Query matchAllQuery = new Query.Builder()
                .matchAll(new MatchAllQuery.Builder().build())
                .build();

        FieldValueFactorScoreFunction fieldValueFactorScoreFunction = new FieldValueFactorScoreFunction.Builder()
                .missing(0.0)
                .factor(10.0)
                .field("rating")
                .build();

        FunctionScore functionScore = new FunctionScore.Builder().fieldValueFactor(fieldValueFactorScoreFunction)
                .build();

        FunctionScoreQuery functionScoreQuery = new FunctionScoreQuery.Builder()
                .query(matchAllQuery)
                .functions(List.of(functionScore))
                .boostMode(FunctionBoostMode.Multiply)
                .scoreMode(FunctionScoreMode.Sum)
                .build();

        Query query = new Query.Builder().functionScore(functionScoreQuery).build();

        SearchRequest request = new SearchRequest.Builder()
                .index(samuraiIndex)
                .source(SourceConfig.of(s -> s.filter(f -> f.includes(List.of("title", "rating")))))
                .query(query)
                .build();
        SearchResponse<SamuraiDocument> searchResponse = openSearchClient.search(request, SamuraiDocument.class);
        return searchResponse.documents();
    }
}
