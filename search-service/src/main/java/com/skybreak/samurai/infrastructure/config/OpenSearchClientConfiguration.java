package com.skybreak.samurai.infrastructure.config;

import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder;
import org.apache.hc.core5.http.HttpHost;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.OpenSearchTransport;
import org.opensearch.client.transport.httpclient5.ApacheHttpClient5TransportBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenSearchClientConfiguration {

    @Bean
    public OpenSearchClient openSearchClient() {
        final HttpHost host = new HttpHost("http", "localhost", 9200);
        final OpenSearchTransport transport = createOpenSearchTransport(host);
        return new OpenSearchClient(transport);
    }

    private static OpenSearchTransport createOpenSearchTransport(HttpHost host) {
        return ApacheHttpClient5TransportBuilder
                .builder(host)
                .setHttpClientConfigCallback(httpClientBuilder ->
                        httpClientBuilder.setConnectionManager(
                                PoolingAsyncClientConnectionManagerBuilder.create().build()))
                .build();
    }

}
