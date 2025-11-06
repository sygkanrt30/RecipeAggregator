package ru.practice.recipe_aggregator.recipe_management.model.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.practice.recipe_aggregator.recipe_management.model.entity.elasticsearch.RecipeDoc;
import ru.practice.shared.dto.RecipeDto;

import java.time.Duration;
import java.util.UUID;

@Component("mapper")
@Mapper(componentModel = "spring")
public interface RecipeMapper {

    @Mapping(target = "minsForPreparing", expression = "java(minutesToDuration(recipe.getMinsForPreparing()))")
    @Mapping(target = "minsForCooking", expression = "java(minutesToDuration(recipe.getMinsForCooking()))")
    @Mapping(target = "additionalMins", expression = "java(minutesToDuration(recipe.getAdditionalMins()))")
    @Mapping(target = "totalMins", expression = "java(minutesToDuration(recipe.getTotalMins()))")
    RecipeDto toRecipeDto(RecipeDoc recipe);

    @Mapping(target = "id", expression = "java(generateRecipeId())")
    @Mapping(target = "minsForPreparing", expression = "java(durationToMinutes(kafkaDto.minsForPreparing()))")
    @Mapping(target = "minsForCooking", expression = "java(durationToMinutes(kafkaDto.minsForCooking()))")
    @Mapping(target = "additionalMins", expression = "java(durationToMinutes(kafkaDto.additionalMins()))")
    @Mapping(target = "totalMins", expression = "java(durationToMinutes(kafkaDto.totalMins()))")
    RecipeDoc fromRecipeKafkaDto(RecipeDto kafkaDto);

    default UUID generateRecipeId() {
        return UUID.randomUUID();
    }

    default int durationToMinutes(Duration duration) {
        return duration != null ? (int) duration.toMinutes() : 0;
    }

    default Duration minutesToDuration(int minutes) {
        return Duration.ofMinutes(minutes);
    }
}
