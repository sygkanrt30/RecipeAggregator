package ru.practice.parser_service.service.parsers.part_of_recipes;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import ru.practice.parser_service.service.parsers.recipe.recipes_parts.DirectionParser;

import static org.junit.jupiter.api.Assertions.*;

class DirectionParserTest {
    @Test
    void parseDirections_shouldReturnEmptyString_whenNoDirectionsFound() {
        // Arrange
        var html = "<div>No directions here</div>";
        Document doc = Jsoup.parse(html);

        // Act
        String result = DirectionParser.parse(doc);

        // Assert
        assertEquals("", result);
    }

    @Test
    void parseDirections_shouldParseSingleStep() {
        // Arrange
        var html = "<ol>" +
                "<li class='mntl-sc-block-group--LI'>" +
                "<p class='mntl-sc-block-html'>Preheat oven to 350°F (175°C)</p>" +
                "</li>" +
                "</ol>";
        Document doc = Jsoup.parse(html);

        // Act
        String result = DirectionParser.parse(doc);

        // Assert
        assertEquals("Preheat oven to 350°F (175°C)", result);
    }

    @Test
    void parseDirections_shouldParseMultipleSteps() {
        // Arrange
        var html = "<ol>" +
                "<li class='mntl-sc-block-group--LI'>" +
                "<p class='mntl-sc-block-html'>Preheat oven to 350°F (175°C)</p>" +
                "</li>" +
                "<li class='mntl-sc-block-group--LI'>" +
                "<p class='mntl-sc-block-html'>Mix flour and sugar in a bowl</p>" +
                "</li>" +
                "<li class='mntl-sc-block-group--LI'>" +
                "<p class='mntl-sc-block-html'>Bake for 30 minutes</p>" +
                "</li>" +
                "</ol>";
        Document doc = Jsoup.parse(html);

        // Act
        var result = DirectionParser.parse(doc);

        // Assert
        var expected = """
                Preheat oven to 350°F (175°C)
                Mix flour and sugar in a bowl
                Bake for 30 minutes""";
        assertEquals(expected, result);
    }

    @Test
    void parseDirections_shouldTrimWhitespace() {
        // Arrange
        var html = "<ol>" +
                "<li class='mntl-sc-block-group--LI'>" +
                "<p class='mntl-sc-block-html'>   Preheat oven to 350°F (175°C)   </p>" +
                "</li>" +
                "<li class='mntl-sc-block-group--LI'>" +
                "<p class='mntl-sc-block-html'>  Mix ingredients  </p>" +
                "</li>" +
                "</ol>";
        Document doc = Jsoup.parse(html);

        // Act
        String result = DirectionParser.parse(doc);

        // Assert
        var expected = "Preheat oven to 350°F (175°C)\n" +
                "Mix ingredients";
        assertEquals(expected, result);
    }

    @Test
    void parseDirections_shouldIgnoreNonDirectionElements() {
        // Arrange
        var html = "<ol>" +
                "<li class='mntl-sc-block-group--LI'>" +
                "<p class='mntl-sc-block-html'>First step</p>" +
                "<div class='ad'>Advertisement</div>" +
                "</li>" +
                "<li class='mntl-sc-block-group--LI'>" +
                "<p>Not a direction</p>" +
                "<p class='mntl-sc-block-html'>Second step</p>" +
                "</li>" +
                "</ol>";
        Document doc = Jsoup.parse(html);

        // Act
        String result = DirectionParser.parse(doc);

        // Assert
        var expected = "First step\nSecond step";
        assertEquals(expected, result);
    }

    @Test
    void parseDirections_shouldHandleEmptySteps() {
        // Arrange
        var html = "<ol>" +
                "<li class='mntl-sc-block-group--LI'>" +
                "<p class='mntl-sc-block-html'></p>" +
                "</li>" +
                "<li class='mntl-sc-block-group--LI'>" +
                "<p class='mntl-sc-block-html'>Actual step</p>" +
                "</li>" +
                "</ol>";
        Document doc = Jsoup.parse(html);

        // Act
        String result = DirectionParser.parse(doc);

        // Assert
        var expected = "\nActual step";
        assertEquals(expected, result);
    }
}
