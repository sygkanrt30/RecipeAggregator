package ru.practice.recipe_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practice.recipe_service.model.dto.mapper.RecipeMapper;
import ru.practice.recipe_service.model.dto.kafka.RecipeKafkaDto;
import ru.practice.recipe_service.model.dto.response.RecipeResponseDto;
import ru.practice.recipe_service.model.entity.RecipeEntity;
import ru.practice.recipe_service.service.entity.RecipeEntityService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class RecipeServiceImpl implements RecipeService, ConsumerProcessor {
    private static final int BATCHES_SIZE = 1000;
    private final RecipeMapper recipeMapper;
    private final RecipeEntityService recipeEntityService;

    @Override
    public RecipeResponseDto findRecipeByName(String name) {
        var recipe = recipeEntityService.findByName(name).orElseThrow(EntityNotFoundException::new);
        return recipeMapper.toRecipeResponseDto(recipe);
    }

    @Override
    public void deleteRecipeByName(String username) {
        recipeEntityService.deleteByName(username.trim());
    }

    @Override
    @Transactional
    public void saveFromKafka(List<RecipeKafkaDto> recipesKafkaDto) {
        if (recipesKafkaDto.isEmpty()) {
            return;
        }

        Set<String> nameOfEntitiesFromDb = recipeEntityService.findAll().stream()
                .map(RecipeEntity::getName)
                .collect(Collectors.toCollection(HashSet::new));

        List<RecipeEntity> newRecipes = recipesKafkaDto.stream()
                .filter(dto -> !nameOfEntitiesFromDb.contains(dto.name()))
                .map(recipeMapper::fromRecipeKafkaDto)
                .toList();

        if (!newRecipes.isEmpty()) {
            int batchSize = Math.min(newRecipes.size(), BATCHES_SIZE);
            recipeEntityService.saveAllWithBatches(newRecipes, batchSize);
        }
    }
}
