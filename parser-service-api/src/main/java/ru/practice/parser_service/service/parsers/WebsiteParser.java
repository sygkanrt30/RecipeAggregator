package ru.practice.parser_service.service.parsers;

import ru.practice.parser_service.model.Recipe;

import java.util.List;

public interface WebsiteParser {
    List<Recipe> parseWebsite(String url);
}
