package ru.practice.parser_service.service.parsers.part_of_recipes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.practice.parser_service.service.parsers.recipe.part_of_recipe.TimeParser;

public class TimeParserTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "25 mins",
            "1 min",
            "23123123 mins",
    })
    @DisplayName("Проверяем как спарситься строка в которой есть только минуты")
    void parseTimeToDuration_shouldReturnDurationWithMinutes(String time) {
        var duration = parseMinutes(time);

        int result = TimeParser.parseDurationFromString(time);

        Assertions.assertEquals(duration, result);
        Assertions.assertDoesNotThrow(() -> TimeParser.parseDurationFromString(time));
    }

    private int parseMinutes(String time) {
        return Integer.parseInt(time.substring(0, time.indexOf("m")).trim());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "3 hrs",
            "1 hr",
            "3232131 hrs",
    })
    @DisplayName("Проверяем как спарситься строка в которой есть только часы")
    void parseTimeToDuration_shouldReturnDurationWithHours(String time) {
        var duration = parseHours(time);

        int result = TimeParser.parseDurationFromString(time);

        Assertions.assertEquals(duration, result);
        Assertions.assertDoesNotThrow(() -> TimeParser.parseDurationFromString(time));
    }

    private int parseHours(String time) {
        return Integer.parseInt(time.substring(0, time.indexOf("h")).trim());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "3 hrs 12 mins",
            "1 hr 1 min",
            "3232131 hrs 312313 mins",
    })
    @DisplayName("Проверяем как спарситься строка в которой есть и часы, и минуты")
    void parseTimeToDuration_shouldReturnDurationWithMinutesAndHours(String time) {
        var duration = parseHours(time) * 60 + parseMinutesInFullString(time);

        int result = TimeParser.parseDurationFromString(time);

        Assertions.assertEquals(duration, result);
        Assertions.assertDoesNotThrow(() -> TimeParser.parseDurationFromString(time));
    }

    private int parseMinutesInFullString(String time) {
        return Integer.parseInt(time.substring(time.indexOf("h") + 3, time.indexOf("m")).trim());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "   ",
            "i WAdf 2 rhr234 ",
    })
    @DisplayName("Проверяем корректно ли спарситься некорректные строки строка")
    void parseTimeToDuration_shouldReturnEmptyString(String s) {
        var duration = 0;

        int result = TimeParser.parseDurationFromString(s);

        Assertions.assertEquals(duration, result);
        Assertions.assertDoesNotThrow(() -> TimeParser.parseDurationFromString(""));
    }
}
