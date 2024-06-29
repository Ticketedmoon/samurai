package com.skybreak.samurai.application.service;

import com.skybreak.samurai.application.domain.dto.SamuraiDocument;
import com.skybreak.samurai.application.domain.model.SearchParams;
import io.micrometer.common.util.StringUtils;
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
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.opensearch.client.opensearch.core.search.SourceConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private static final List<String> SEARCHABLE_DOCUMENT_FIELDS
            = List.of("id", "name", "summary", "samurai_rating", "screenshots");
    private static final String WILDCARD_CHARACTER = "*";

    @Value(value = "${search-service.opensearch.read-index.name}")
    private String samuraiIndex;

    private final OpenSearchClient openSearchClient;

    /**
     * Performs a search against the document store by the search params specified.
     *
     * @param searchParams The search params
     * @return A List of documents that matches the search request
     * @throws IOException Thrown for malformed search requests
     */
    public List<SamuraiDocument> performSearch(SearchParams searchParams) throws IOException {
        handleSearchParameterOverrides(searchParams);
        SearchRequest request = buildSearchRequest(searchParams);
        SearchResponse<SamuraiDocument> searchResponse = openSearchClient.search(request, SamuraiDocument.class);
        return searchResponse.hits().hits().stream()
                .map(Hit::source)
                .toList();
    }

    private static void handleSearchParameterOverrides(SearchParams searchParams) {
        if (StringUtils.isEmpty(searchParams.getQ())) {
            searchParams.setQ(WILDCARD_CHARACTER);
        }
    }

    private SearchRequest buildSearchRequest(SearchParams searchParams) {
        Query matchAllQuery = new Query.Builder()
                .queryString(qs -> qs.fields("name", "summary").query(searchParams.getQ()))
                .build();

        FieldValueFactorScoreFunction fieldValueFactorScoreFunction = new FieldValueFactorScoreFunction.Builder()
                .missing(0.0)
                .factor(10.0)
                .field("samurai_rating")
                .build();

        FunctionScore functionScore = new FunctionScore.Builder()
                .fieldValueFactor(fieldValueFactorScoreFunction)
                .build();

        FunctionScoreQuery functionScoreQuery = new FunctionScoreQuery.Builder()
                .query(matchAllQuery)
                .functions(List.of(functionScore))
                .boostMode(FunctionBoostMode.Multiply)
                .scoreMode(FunctionScoreMode.Sum)
                .build();

        Query query = new Query.Builder()
                .functionScore(functionScoreQuery).build();

        return new SearchRequest.Builder()
                .index(samuraiIndex)
                .source(SourceConfig.of(
                        function -> function.filter(filter -> filter.includes(SEARCHABLE_DOCUMENT_FIELDS))))
                .query(query)
                .size(searchParams.getRows())
                .build();
    }
}
