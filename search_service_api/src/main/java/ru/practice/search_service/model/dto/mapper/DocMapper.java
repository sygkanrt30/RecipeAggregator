package ru.practice.search_service.model.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.practice.search_service.model.entity.elasticsearch.IngredientDoc;
import ru.practice.search_service.model.entity.elasticsearch.RecipeDoc;
import ru.practice.search_service.model.entity.postgres.IngredientEntity;
import ru.practice.search_service.model.entity.postgres.RecipeEntity;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface DocMapper {
    @Mapping(target = "ingredients", expression = "java(toIngredientDocList(recipeEntity.getIngredients()))")
    RecipeDoc toRecipeDoc(RecipeEntity recipeEntity);

    default List<IngredientDoc> toIngredientDocList(List<IngredientEntity> ingredientEntities) {
        return ingredientEntities.stream()
                .map(entity -> IngredientDoc.builder()
                        .id(entity.getId())
                        .name(entity.getName())
                        .unit(entity.getUnit())
                        .quantity(entity.getQuantity())
                        .build())
                .toList();
    }
}
