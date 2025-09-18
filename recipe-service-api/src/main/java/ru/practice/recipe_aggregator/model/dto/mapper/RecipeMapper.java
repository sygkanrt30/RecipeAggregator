package ru.practice.recipe_aggregator.model.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.practice.recipe_aggregator.model.dto.kafka.RecipeKafkaDto;
import ru.practice.recipe_aggregator.model.dto.response.RecipeResponseDto;
import ru.practice.recipe_aggregator.model.entity.elasticsearch.IngredientDoc;
import ru.practice.recipe_aggregator.model.entity.elasticsearch.RecipeDoc;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
@Component
public interface RecipeMapper {
    RecipeResponseDto toRecipeResponseDto(RecipeDoc recipe);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ingredients", expression = "java(getListOfRecipesFromMap(kafkaDto.ingredients()))")
    RecipeDoc fromRecipeKafkaDto(RecipeKafkaDto kafkaDto);

    default List<IngredientDoc> getListOfRecipesFromMap(Map<String, String> map) {
        return map.entrySet().stream()
                .map(entry -> {
                    var parts = entry.getValue().split(" ");
                    if (parts.length == 2) {
                        return IngredientDoc.builder()
                                .name(entry.getKey())
                                .quantity(parts[0])
                                .unit(parts[1])
                                .build();
                    }
                    return IngredientDoc.builder()
                            .name(entry.getKey())
                            .quantity(parts[0])
                            .build();
                })
                .toList();
    }
}
