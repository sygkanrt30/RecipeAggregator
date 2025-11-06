package ru.practice.recipe_aggregator.recipe_management.recipe_service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practice.recipe_aggregator.recipe_management.model.dto.mapper.RecipeMapper;
import ru.practice.recipe_aggregator.recipe_management.model.entity.elasticsearch.RecipeDoc;
import ru.practice.shared.dto.RecipeDto;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class RecipeConsumerProcessor implements ConsumerProcessor<List<RecipeDto>> {
    private final RecipeMapper recipeMapper;
    private final RecipeService recipeEntityService;

    @Override
    @Transactional
    public void saveFromKafka(List<RecipeDto> recipesKafkaDto) {
        log.debug("Numbers of recipes from kafka: {}", recipesKafkaDto.size());
        if (recipesKafkaDto.isEmpty()) {
            log.warn("No recipes read from kafka");
            return;
        }

        var nameOfEntitiesFromDb = recipeEntityService.findAll().stream()
                .map(RecipeDoc::getName)
                .collect(Collectors.toCollection(HashSet::new));

        var newRecipes = recipesKafkaDto.stream()
                .filter(dto -> !nameOfEntitiesFromDb.contains(dto.name()))
                .map(recipeMapper::fromRecipeKafkaDto)
                .toList();
        log.debug("Number of recipes from kafka if not contains in elasticsearch: {}", newRecipes.size());

        if (!newRecipes.isEmpty()) {
            recipeEntityService.saveAllWithBatches(newRecipes);
            return;
        }
        log.warn("All recipes already exist in elasticsearch");
    }
}