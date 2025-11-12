package ru.practice.parser_service.service.parsers.recipe.recipes_parts;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@UtilityClass
@Slf4j
public class TimeParser {

    public Duration parseMinsFromString(String timeStr) {
        log.debug("parse(doc): Start parsing time from string: {}", timeStr);
        String normalized = timeStr.toLowerCase()
                .replaceAll("hrs?\\.?", "H")
                .replaceAll("mins?\\.?", "M")
                .replaceAll("\\s+", "");
        var duration = Duration.parse("PT" + normalized);
        log.debug("parse(doc): Parsed duration: {}", duration);
        return duration;
    }
}
