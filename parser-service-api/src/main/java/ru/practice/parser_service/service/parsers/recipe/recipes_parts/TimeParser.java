package ru.practice.parser_service.service.parsers.recipe.recipes_parts;

import lombok.experimental.UtilityClass;

import java.util.regex.Pattern;

@UtilityClass
public class TimeParser {
    private static final Pattern TIME_PATTERN = Pattern.compile("(?:((\\d+) hrs?)\\s*)?((\\d+) mins?)?");

    public int parseMinsFromString(String timeStr) {
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
        return hours * 60 + minutes;
    }
}
