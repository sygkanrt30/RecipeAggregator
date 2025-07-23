package ru.practice.parser_service.service.parsers.part_of_recipes;

import lombok.experimental.UtilityClass;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class TimeParser {
    private static final Pattern TIME_PATTERN = Pattern.compile("(?:((\\d+) hrs?)\\s*)?((\\d+) mins?)?");

    public Duration parseDurationFromString(String timeStr) {
        Matcher matcher = TIME_PATTERN.matcher(timeStr);
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
        return Duration.ofHours(hours).plusMinutes(minutes);
    }
}
