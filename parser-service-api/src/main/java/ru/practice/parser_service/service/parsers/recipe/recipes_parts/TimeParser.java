package ru.practice.parser_service.service.parsers.recipe.recipes_parts;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.regex.Pattern;

@UtilityClass
@Slf4j
public class TimeParser {

    private static final Pattern TIME_PATTERN = Pattern.compile("(?:((\\d+) hrs?)\\s*)?((\\d+) mins?)?");

    public Duration parseMinsFromString(String timeStr) {
        log.trace("Start parsing direction from string {}", timeStr);
        var matcher = TIME_PATTERN.matcher(timeStr);
        int hours = 0;
        int minutes = 0;
        if (matcher.find()) {
            if (matcher.group(1) != null) {
                hours = Integer.parseInt(matcher.group(2));
            }
            if (matcher.group(3) != null) {
                minutes = Integer.parseInt(matcher.group(4));
            }
        }
        int timeInMinutes = hours * 60 + minutes;
        log.trace("Parsed minutes: {}", timeInMinutes);
        return Duration.ofMinutes(timeInMinutes);
    }
}
