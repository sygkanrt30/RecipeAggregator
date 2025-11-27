package ru.practice.parser_service.service.parsers.recipe.html.recipe_parts;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.format.DateTimeParseException;

@UtilityClass
@Slf4j
public class TimeParser {

    private static final Duration DEFAULT_VALUE = Duration.ofMinutes(0);

    public Duration parseMinsFromString(String timeStr) {
        log.trace("parseMinsFromString(timeStr): Start parsing time from string: {}", timeStr);
        String normalized = timeStr.toLowerCase()
                .replaceAll("hrs?\\.?", "H")
                .replaceAll("mins?\\.?", "M")
                .replaceAll("\\s+", "");
        try {
            var duration = Duration.parse("PT" + normalized);
            log.debug("parseMinsFromString(timeStr): Parsed duration: {}", duration);
            return duration;
        } catch (DateTimeParseException e) {
            log.warn("parseMinsFromString(timeStr): Invalid time({}) cause: {}", timeStr, e.getMessage());
            return DEFAULT_VALUE;
        }
    }
}
