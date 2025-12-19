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
@ConfigurationProperties(prefix = "parser.browser")
public class BrowserConfig {

    @NotNull
    private String userAgent;

    @NotNull
    private String referrer;

    @NotNull
    private String mainUrl;

    @NotNull
    private String headerAccept;

    @NotNull
    private String headerCookie;

    @PostConstruct
    public void init() {
        log.debug("BROWSER CONFIG: userAgent={}, mainUrl={}",
                userAgent, mainUrl);
    }
}
