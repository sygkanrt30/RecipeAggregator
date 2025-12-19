package ru.practice.parser_service.service.parsers.recipe.jsonld.recipe_parts;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

@Slf4j
@UtilityClass
public class ServingsParser {

    private static final int DEFAULT_VALUE = 1;
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+");

    public int parseServings(String servingsStr) {
        if (servingsStr == null) {
            return DEFAULT_VALUE;
        }
        var str = servingsStr.trim();
        if (str.isEmpty()) {
            return DEFAULT_VALUE;
        }

        if (isJsonArray(str)) {
            str = str.substring(1, str.length() - 1)
                    .split(",")[0]
                    .replace("\"", "")
                    .trim();
        }
        return parseFromString(str);
    }

    private boolean isJsonArray(String string) {
        return string.startsWith("[") && string.endsWith("]");
    }

    private int parseFromString(String str) {
        if (isInvalidString(str)) {
            return DEFAULT_VALUE;
        }
        try {
            var matcher = NUMBER_PATTERN.matcher(str);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group());
            }
        } catch (Exception e) {
            log.debug("Failed to parse servings: {}", str);
        }
        return DEFAULT_VALUE;
    }

    private boolean isInvalidString(String str) {
        return str.isEmpty() || str.equalsIgnoreCase("null") ||
                str.equalsIgnoreCase("N/A") || str.equalsIgnoreCase("no servings");
    }
}
