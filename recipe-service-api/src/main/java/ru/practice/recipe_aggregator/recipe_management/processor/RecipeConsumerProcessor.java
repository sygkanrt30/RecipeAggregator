package ru.practice.recipe_aggregator.recipe_management.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practice.recipe_aggregator.recipe_management.model.dto.mapper.RecipeMapper;
import ru.practice.recipe_aggregator.recipe_management.recipe_service.RecipeService;
import ru.practice.shared.dto.RecipeDto;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class RecipeConsumerProcessor implements ConsumerProcessor<List<RecipeDto>> {

    private final RecipeMapper recipeMapper;
    private final RecipeService recipeService;

    @Override
    @Transactional
    public void saveFromKafka(List<RecipeDto> recipesDto) {
        log.debug("Numbers of recipes from kafka: {}", recipesDto.size());
        if (recipesDto.isEmpty()) {
            log.warn("No recipes read from kafka");
            return;
        }

        Set<String> incomingNames = recipesDto.stream()
                .map(RecipeDto::name)
                .collect(Collectors.toSet());
        log.debug("Checking existence for {} recipe names in database", incomingNames.size());

        Set<String> existingNames = recipeService.findExistingNames(incomingNames);
        log.debug("Found {} existing recipes in database", existingNames.size());

        var newRecipes = recipesDto.stream()
                .filter(dto -> !existingNames.contains(dto.name()))
                .map(recipeMapper::fromRecipeDto)
                .toList();

        log.debug("Number of new recipes to save: {}", newRecipes.size());

        if (!newRecipes.isEmpty()) {
            recipeService.saveAllWithBatches(newRecipes);
            return;
        }
        log.warn("All recipes already exist in database");
    }
}