package ru.practice.parser_service.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@Slf4j
@Accessors(chain = true, fluent = true)
public class ParserConfig {
    @Value("${parser.timeout:15000}")
    private int timeout = 15000;

    @Value("${parser.min-delay-ms:1000}")
    private int minDelayMs = 1000;

    @Value("${parser.max-delay-ms:2000}")
    private int maxDelayMs = 2000;

    @Value("${parser.max-links-per-page:50}")
    private int maxLinksPerPage = 50;

    @Value("${parser.max-recipes:25}")
    private int maxRecipes = 25;

    @Value("${parser.max-depth:3}")
    private int maxDepth = 3;

    @Value("${parser.container-selectors:}")
    private String containerSelectors;

    @Value("${parser.recipe-tag:}")
    private String recipeTag;

    @Value("${parser.browser.user-agent:}")
    private String userAgent;

    @Value("${parser.browser.referrer:}")
    private String referrer;

    @Value("${parser.browser.main-url:}")
    private String mainUrl;

    @Value("${parser.browser.header-accept}")
    private String accept;

    @Value("${parser.browser.header-cookie}")
    private String cookie;

    @PostConstruct
    public void init() {
        log.debug("PARSER CONFIG: timeout={}, recipeTag={}, containerSelectors={}",
                timeout, recipeTag, containerSelectors);
        log.debug("BROWSER CONFIG: userAgent={}, mainUrl={}",
                userAgent, mainUrl);
    }
}
