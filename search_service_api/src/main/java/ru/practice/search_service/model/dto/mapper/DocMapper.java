package ru.practice.search_service.model.dto.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.practice.search_service.model.entity.elasticsearch.RecipeDoc;
import ru.practice.search_service.model.entity.postgres.RecipeEntity;

@Mapper(componentModel = "spring")
@Component
public interface DocMapper {
    RecipeDoc toRecipeDoc(RecipeEntity recipeEntity);
}
