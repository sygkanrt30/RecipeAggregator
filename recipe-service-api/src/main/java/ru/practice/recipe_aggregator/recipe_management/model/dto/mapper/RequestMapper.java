package ru.practice.recipe_aggregator.recipe_management.model.dto.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.practice.recipe_aggregator.recipe_management.model.dto.container.FilterCondition;
import ru.practice.recipe_aggregator.recipe_management.model.dto.container.FilterOperator;
import ru.practice.recipe_aggregator.recipe_management.model.dto.container.SearchContainer;
import ru.practice.recipe_aggregator.recipe_management.search_service.controller.SearchRequest;

@Mapper(componentModel = "spring")
@Component
public interface RequestMapper {

    @Mapping(target = "ingredientNames", expression = "java(request.ingredientNames())")
    @Mapping(target = "name", expression = "java(request.name())")
    @Mapping(target = "cookingTimeCondition", expression = "java(mapToFilterCondition(\"cookingTime\", request.cookingTime(), request.cookingTimeOperator()))")
    @Mapping(target = "totalTimeCondition", expression = "java(mapToFilterCondition(\"totalTime\", request.totalTime(), request.totalTimeOperator()))")
    @Mapping(target = "preparationTimeCondition", expression = "java(mapToFilterCondition(\"preparingTime\", request.preparationTime(), request.preparationTimeOperator()))")
    @Mapping(target = "servingsCondition", expression = "java(mapToFilterCondition(\"servings\", request.servings(), request.servingsOperator()))")
    SearchContainer toSearchContainer(SearchRequest request);

    default FilterCondition mapToFilterCondition(String fieldName, int value, String operator) {
        if (value == 0 || operator == null) {
            return null;
        }
        return new FilterCondition(
                fieldName,
                FilterOperator.valueOf(operator.toUpperCase()),
                value
        );
    }
}
