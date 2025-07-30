package ru.practice.recipe_service.model.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.practice.recipe_service.model.dto.request.RecipeKafkaDto;
import ru.practice.recipe_service.model.dto.request.RecipeRestRequestDto;
import ru.practice.recipe_service.model.dto.response.RecipeResponseDto;
import ru.practice.recipe_service.model.entity.IngredientEntity;
import ru.practice.recipe_service.model.entity.RecipeEntity;

import java.util.ArrayList;
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

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "totalMins", expression = "java(getTotalMins(requestDto.additionalMins(), requestDto.mins4Cook(), requestDto.mins4Prep()))")
    RecipeEntity fromRecipeRestRequestDto(RecipeRestRequestDto requestDto);

    default int getTotalMins(int additionalMins, int mins4Cook, int mins4Prep) {
        return mins4Prep + additionalMins + mins4Cook;
    }

    default List<IngredientEntity> getListOfRecipesFromMap(Map<String, String> map) {
        var recipes = new ArrayList<IngredientEntity>();
        for (var entry : map.entrySet()) {
            var value = entry.getValue().split(" ");
            var ingredient = IngredientEntity.builder()
                    .name(entry.getKey())
                    .quantity(value[0])
                    .unit(value[1])
                    .build();
            recipes.add(ingredient);
        }
        return recipes;
    }
}
