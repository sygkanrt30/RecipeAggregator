package ru.practice.parser_service.service.parsers.recipe.jsonld.recipe_parts;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ServingsParserTest {

    @ParameterizedTest
    @CsvSource({
            "'10', 10",
            "'4', 4",
            "'1', 1",
            "'0', 0"
    })
    void shouldParseSimpleNumbers(String input, int expected) {
        assertEquals(expected, ServingsParser.parseServings(input));
    }

    @ParameterizedTest
    @CsvSource({
            "'10 servings', 10",
            "'4 people', 4",
            "'2 portions', 2",
            "'1 serving', 1"
    })
    void shouldParseNumbersWithText(String input, int expected) {
        assertEquals(expected, ServingsParser.parseServings(input));
    }

    @ParameterizedTest
    @CsvSource({
            "'3-4', 3",
            "'4-6 servings', 4",
            "'8-10 people', 8",
            "'1-2 portions', 1"
    })
    void shouldParseRanges(String input, int expected) {
        assertEquals(expected, ServingsParser.parseServings(input));
    }

    @ParameterizedTest
    @CsvSource({
            "'1 (9x13-inch) casserole', 1",
            "'4 (1/2 cup) servings', 4",
            "'6 (approx)', 6",
            "'2 (large) portions', 2"
    })
    void shouldParseNumbersWithParentheses(String input, int expected) {
        assertEquals(expected, ServingsParser.parseServings(input));
    }

    @ParameterizedTest
    @CsvSource({
            "'[\"10\", \"1 (9x13-inch) casserole\"]', 10",
            "'[\"4\", \"servings\"]', 4",
            "'[\"6-8\"]', 6",
            "'[\"1\"]', 1"
    })
    void shouldParseJsonArrayFormat(String input, int expected) {
        assertEquals(expected, ServingsParser.parseServings(input));
    }

    @ParameterizedTest
    @CsvSource({
            "'', 1",
            "'null', 1",
            "'servings', 1",
            "'no servings', 1",
            "'N/A', 1"
    })
    void shouldReturnDefaultForInvalidInput(String input, int expected) {
        assertEquals(expected, ServingsParser.parseServings(input));
    }

    @ParameterizedTest
    @CsvSource({
            "'10б', 10",
            "'4порции', 4",
            "'6servings', 6",
            "'2-3порции', 2"
    })
    void shouldParseWithNonLatinCharacters(String input, int expected) {
        assertEquals(expected, ServingsParser.parseServings(input));
    }

    @Test
    void shouldParseComplexFormats() {
        assertEquals(12, ServingsParser.parseServings("12 (3x4) mini casseroles"));
        assertEquals(8, ServingsParser.parseServings("8-12 servings depending on size"));
        assertEquals(6, ServingsParser.parseServings("6 (or more) portions"));
        assertEquals(4, ServingsParser.parseServings("4 large or 6 small servings"));
    }

    @ParameterizedTest
    @CsvSource({
            "' 10 ', 10",
            "'  4-6  ', 4",
            "'\t8\t', 8",
            "'\n2\n', 2"
    })
    void shouldHandleWhitespace(String input, int expected) {
        assertEquals(expected, ServingsParser.parseServings(input));
    }

    @Test
    void shouldHandleNullInput() {
        assertEquals(1, ServingsParser.parseServings(null));
    }

    @Test
    void shouldHandleVeryLargeNumbers() {
        assertEquals(999, ServingsParser.parseServings("999 servings"));
    }

    @Test
    void shouldHandleSpecialCharacters() {
        assertEquals(4, ServingsParser.parseServings("4$#@!servings"));
        assertEquals(2, ServingsParser.parseServings("2***"));
    }
}
