package ru.practice.parser_service.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.practice.shared.dto.RecipeDto;
import ru.practice.shared.dto.ingredient.IngredientDto;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
@Component("mapper")
public interface RecipeMapper {

    @Mapping(target = "name", expression = "java(name.trim().toLowerCase())")
    RecipeDto toRecipeDto(
            UUID id,
            String name,
            Duration timeForPreparing,
            Duration timeForCooking,
            Duration totalTime,
            int servings,
            List<IngredientDto> ingredients,
            String direction,
            String description
    );

}
