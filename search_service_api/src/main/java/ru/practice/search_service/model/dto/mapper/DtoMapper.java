package ru.practice.search_service.model.dto.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.practice.search_service.model.dto.response.RecipeResponseDto;
import ru.practice.search_service.model.entity.elasticsearch.RecipeDoc;

@Mapper(componentModel = "spring")
@Component
public interface DtoMapper {
    RecipeResponseDto toRecipeResponseDto(RecipeDoc recipe);
}
