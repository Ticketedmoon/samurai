package com.skybreak.samurai.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.skybreak.samurai.application.domain.dto.SamuraiDocument;
import com.skybreak.samurai.application.domain.model.SearchParams;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.opensearch.client.opensearch.core.search.HitsMetadata;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

	@Mock
	private OpenSearchClient openSearchClient;


	// cut = 'class-under-test'
	@InjectMocks
	private SearchService cut;

	@AfterEach
	void tearDown() {
		verifyNoMoreInteractions(openSearchClient);
	}

	@Test
	void given_inbound_search_request_when_perform_search_then_should_return_search_result(
			@Mock Hit<SamuraiDocument> hit,
			@Mock HitsMetadata<SamuraiDocument> hitsMetadata,
			@Mock SearchResponse<SamuraiDocument> searchResponse) throws IOException {
		SearchParams searchParams = SearchParams.builder()
				.q("cats")
				.language("en")
				.build();
		SamuraiDocument expectedDocument = SamuraiDocument.builder()
				.id(1)
				.name("test document")
				.summary("test summary for test document")
				.rating(BigDecimal.valueOf(75.0))
				.build();

		given(hit.source()).willReturn(expectedDocument);
		given(hitsMetadata.hits()).willReturn(List.of(hit));
		given(searchResponse.hits()).willReturn(hitsMetadata);
		given(openSearchClient.search(any(SearchRequest.class), any(Class.class))).willReturn(searchResponse);
		List<SamuraiDocument> documents = cut.performSearch(searchParams);

		assertThat(documents).isNotEmpty();
		assertThat(documents.size()).isEqualTo(1);

		SamuraiDocument actualDocument = documents.getFirst();
		assertThat(actualDocument).isEqualTo(expectedDocument);

		verify(openSearchClient).search(any(SearchRequest.class), eq(SamuraiDocument.class));
		verify(searchResponse).hits();
		verify(hitsMetadata).hits();
		verify(hit).source();
	}

	@Test
	void given_inbound_search_request_when_perform_search_then_should_throw_exception_for_malformed_request() {
	}
}
