package com.skybreak.samurai.application.service;

import com.skybreak.samurai.application.domain.dto.SamuraiDocument;
import java.io.IOException;

public interface IndexingService {

    /**
     * Perform index operation for document.
     *
     * @param document The document to index
     */
    void performIndex(SamuraiDocument document) throws IOException;

}
