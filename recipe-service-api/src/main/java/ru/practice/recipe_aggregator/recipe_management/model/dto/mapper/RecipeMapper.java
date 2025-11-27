package ru.practice.recipe_aggregator.recipe_management.model.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.practice.recipe_aggregator.recipe_management.model.entity.elasticsearch.RecipeDoc;
import ru.practice.shared.dto.RecipeDto;

import java.time.Duration;

@Component
@Mapper(componentModel = "spring")
public interface RecipeMapper {

    @Mapping(target = "timeForPreparing", expression = "java(minutesToDuration(recipe.getMinsForPreparing()))")
    @Mapping(target = "timeForCooking", expression = "java(minutesToDuration(recipe.getMinsForCooking()))")
    @Mapping(target = "totalTime", expression = "java(minutesToDuration(recipe.getTotalMins()))")
    RecipeDto toRecipeDto(RecipeDoc recipe);

    @Mapping(target = "minsForPreparing", expression = "java(durationToMinutes(kafkaDto.timeForPreparing()))")
    @Mapping(target = "minsForCooking", expression = "java(durationToMinutes(kafkaDto.timeForCooking()))")
    @Mapping(target = "totalMins", expression = "java(durationToMinutes(kafkaDto.totalTime()))")
    RecipeDoc fromRecipeDto(RecipeDto kafkaDto);

    default int durationToMinutes(Duration duration) {
        return duration != null ? (int) duration.toMinutes() : 0;
    }

    default Duration minutesToDuration(int minutes) {
        return Duration.ofMinutes(minutes);
    }
}
