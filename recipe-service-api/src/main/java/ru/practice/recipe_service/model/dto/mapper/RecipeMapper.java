package ru.practice.recipe_service.model.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.practice.recipe_service.model.dto.kafka.request.RecipeKafkaDto;
import ru.practice.recipe_service.model.dto.response.RecipeResponseDto;
import ru.practice.recipe_service.model.entity.IngredientEntity;
import ru.practice.recipe_service.model.entity.RecipeEntity;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
@Component
public interface RecipeMapper {
    RecipeResponseDto toRecipeResponseDto(RecipeEntity recipe);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "ingredients", expression = "java(getListOfRecipesFromMap(kafkaDto.ingredients()))")
    RecipeEntity fromRecipeKafkaDto(RecipeKafkaDto kafkaDto);

    @SuppressWarnings("unused")
    default List<IngredientEntity> getListOfRecipesFromMap(Map<String, String> map) {
        return map.entrySet().stream()
                .map(entry -> {
                    var parts = entry.getValue().split(" ");
                    if (parts.length == 2) {
                        return IngredientEntity.builder()
                                .name(entry.getKey())
                                .quantity(parts[0])
                                .unit(parts[1])
                                .build();
                    }
                    return IngredientEntity.builder()
                            .name(entry.getKey())
                            .quantity(parts[0])
                            .build();
                })
                .toList();
    }
}
