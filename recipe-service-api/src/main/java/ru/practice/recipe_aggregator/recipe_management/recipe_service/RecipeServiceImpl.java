package ru.practice.recipe_aggregator.recipe_management.recipe_service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practice.recipe_aggregator.recipe_management.model.dto.kafka.RecipeKafkaDto;
import ru.practice.recipe_aggregator.recipe_management.model.dto.mapper.RecipeMapper;
import ru.practice.recipe_aggregator.recipe_management.model.dto.response.RecipeResponseDto;
import ru.practice.recipe_aggregator.recipe_management.model.entity.elasticsearch.RecipeDoc;
import ru.practice.recipe_aggregator.recipe_management.recipe_service.entity.RecipeEntityService;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class RecipeServiceImpl implements RecipeService, ConsumerProcessor {
    private static final int BATCHES_SIZE = 10;
    private final RecipeMapper recipeMapper;
    private final RecipeEntityService recipeEntityService;

    @Override
    public RecipeResponseDto findRecipeByName(String name) {
        var recipe = recipeEntityService.findByName(name).orElseThrow(EntityNotFoundException::new);
        return recipeMapper.toRecipeResponseDto(recipe);
    }

    @Override
    public List<RecipeResponseDto> findAllByIds(List<UUID> recipeIds) {
        return recipeEntityService.findAllByIds(recipeIds).stream()
                .map(recipeMapper::toRecipeResponseDto)
                .toList();
    }

    @Override
    public UUID getIdByName(String recipeName) {
        return recipeEntityService.findByName(recipeName)
                .orElseThrow(() -> new EntityNotFoundException("There is no recipe with that name"))
                .getId();
    }

    @Override
    @Transactional
    public void saveFromKafka(List<RecipeKafkaDto> recipesKafkaDto) {
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
            int batchSize = Math.min(newRecipes.size(), BATCHES_SIZE);
            recipeEntityService.saveAllWithBatches(newRecipes, batchSize);
            return;
        }
        log.warn("All recipes already exist in elasticsearch");
    }
}