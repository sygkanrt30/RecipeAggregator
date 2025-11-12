package ru.practice.parser_service.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.practice.shared.dto.IngredientDto;
import ru.practice.shared.dto.RecipeDto;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
@Component("mapper")
public interface RecipeMapper {

    @Mapping(target = "name", expression = "java(name.trim().toLowerCase())")
    @Mapping(target = "id", expression = "java(generateRecipeId())")
    RecipeDto toRecipeDto(
            String name,
            Duration minsForPreparing,
            Duration minsForCooking,
            Duration additionalMins,
            Duration totalMins,
            int servings,
            List<IngredientDto> ingredients,
            String direction,
            String description
    );

    default UUID generateRecipeId() {
        return UUID.randomUUID();
    }
}
