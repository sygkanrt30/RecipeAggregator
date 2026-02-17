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
}
