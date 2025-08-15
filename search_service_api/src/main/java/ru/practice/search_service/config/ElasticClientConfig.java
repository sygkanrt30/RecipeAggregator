package ru.practice.search_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.lang.NonNull;

import java.time.Duration;

@Configuration
@EnableElasticsearchRepositories(basePackages
        = "ru.practice.search_service.repository")
@ComponentScan(basePackages = { "ru.practice.search_service" })
public class ElasticClientConfig extends ElasticsearchConfiguration {
    @Value("${elasticsearch.host-and-port}")
    private String hostAndPort;

    @Override
    public @NonNull ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(hostAndPort)
                .withConnectTimeout(Duration.ofSeconds(5))
                .withSocketTimeout(Duration.ofSeconds(30))
                .build();
    }
}
