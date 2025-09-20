package ru.practice.recipe_aggregator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.lang.NonNull;

import java.time.Duration;

@Configuration
@EnableElasticsearchRepositories(basePackages = "ru.practice.recipe_aggregator.recipe_management.repository")
@ComponentScan(basePackages = {"ru.practice"})
public class ElasticClientConfig extends ElasticsearchConfiguration {
    @Value("${elasticsearch.host-and-port}")
    private String hostAndPort;

    @Value("${spring.elasticsearch.connection-timeout:30000}")
    private long connectionTimeout;

    @Value("${spring.elasticsearch.socket-timeout:60000}")
    private long socketTimeout;

    @Override
    public @NonNull ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(hostAndPort)
                .withConnectTimeout(Duration.ofMillis(connectionTimeout))
                .withSocketTimeout(Duration.ofMillis(socketTimeout))
                .withBasicAuth("", "")
                .build();
    }
}
