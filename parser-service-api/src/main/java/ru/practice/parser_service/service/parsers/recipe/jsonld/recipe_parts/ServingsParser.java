package ru.practice.parser_service.service.parsers.recipe.jsonld.recipe_parts;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

@Slf4j
@UtilityClass
public class ServingsParser {

    private static final int DEFAULT_VALUE = 1;

    public int parseServings(String servingsStr) {
        try {
            if (servingsStr == null || servingsStr.isEmpty()) {
                return DEFAULT_VALUE;
            }
            var cleaned = servingsStr.replaceAll("[\"\\[\\]]", "").trim();
            if (cleaned.matches("\\d+")) {
                return Integer.parseInt(cleaned);
            }
            String[] parts = cleaned.split("\\s+");
            for (var part : parts) {
                String numberPart = part.replaceAll("[^\\d-]", "");
                if (numberPart.matches("\\d+")) {
                    return Integer.parseInt(numberPart);
                }
                if (numberPart.matches("\\d+-\\d+")) {
                    return Integer.parseInt(numberPart.split("-")[0]);
                }
            }
            var pattern = Pattern.compile("\\d+");
            var matcher = pattern.matcher(cleaned);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group());
            }
        } catch (Exception e) {
            log.debug("Failed to parse servings: {}", servingsStr);
        }
        return DEFAULT_VALUE;
    }
}
