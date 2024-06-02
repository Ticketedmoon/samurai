package com.skybreak.samurai.application.service;

import com.skybreak.samurai.application.domain.dto.SamuraiDocument;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.opensearch.indices.ExistsRequest;
import org.opensearch.client.opensearch.indices.IndexSettings;
import org.opensearch.client.opensearch.indices.PutIndicesSettingsRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenSearchIndexingService implements IndexingService {

    @Value(value = "${metadata-indexer-service.opensearch.write-index.name}")
    private String samuraiIndex;

    private final OpenSearchClient openSearchClient;

    @PostConstruct
    private void setUpIndex() throws IOException {

        if (doesIndexExist()) {
            return;
        }

        createIndex();
    }

    @Override
    public void performIndex(SamuraiDocument document) throws IOException {
        IndexRequest<SamuraiDocument> indexRequest = new IndexRequest.Builder<SamuraiDocument>()
                .index(samuraiIndex)
                .id(String.valueOf(document.getId()))
                .document(document)
                .build();
        openSearchClient.index(indexRequest);
        log.info("Document with id {} has been indexed", document.getId());
    }

    private void createIndex() throws IOException {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder().index(samuraiIndex).build();
        openSearchClient.indices().create(createIndexRequest);

        IndexSettings indexSettings = new IndexSettings.Builder().autoExpandReplicas("0-all").build();
        PutIndicesSettingsRequest putIndicesSettingsRequest = new PutIndicesSettingsRequest.Builder()
                .index(samuraiIndex)
                .settings(indexSettings)
                .build();
        openSearchClient.indices().putSettings(putIndicesSettingsRequest);
    }


    private boolean doesIndexExist() throws IOException {
        ExistsRequest existsIndexRequest = new ExistsRequest.Builder().index(samuraiIndex).build();
        return openSearchClient.indices().exists(existsIndexRequest).value();
    }
}
