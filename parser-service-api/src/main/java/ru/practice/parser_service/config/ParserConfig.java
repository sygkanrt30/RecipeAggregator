package ru.practice.parser_service.config;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Accessors(chain = true)
@Slf4j
@Validated
@Configuration
@ConfigurationProperties(prefix = "parser")
public class ParserConfig {

    @NotNull
    private int timeoutMs;

    @NotNull
    private int minDelayMs;

    @NotNull
    private int maxDelayMs;

    @NotNull
    private int maxLinksPerPage;

    @NotNull
    private int maxRecipes;

    private int maxDepth = 3;

    @NotNull
    private String containerSelectors;

    @NotNull
    private String recipeTag;

    @PostConstruct
    public void init() {
        log.debug("PARSER CONFIG: timeout={}, recipeTag={}, containerSelectors={}",
                timeoutMs, recipeTag, containerSelectors);
    }
}
