package ru.practice.parser_service.service.parsers.website;

import ru.practice.parser_service.model.Recipe;

import java.util.List;

public interface WebsiteParser {
    List<Recipe> parse(String url);
}
