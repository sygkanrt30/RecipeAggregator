package ru.practice.parser_service.service.parsers.website;


import ru.practice.shared.dto.RecipeDto;

import java.util.List;

public interface WebsiteParser {

    List<RecipeDto> parse(String url);
}
