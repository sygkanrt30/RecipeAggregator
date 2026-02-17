package ru.practice.parser_service.config;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Data
@Accessors(chain = true)
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
}
