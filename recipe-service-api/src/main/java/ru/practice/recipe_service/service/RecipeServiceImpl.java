package ru.practice.recipe_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practice.recipe_service.model.dto.mapper.RecipeMapper;
import ru.practice.recipe_service.model.dto.request.RecipeKafkaDto;
import ru.practice.recipe_service.model.dto.request.RecipeRestRequestDto;
import ru.practice.recipe_service.model.dto.response.RecipeResponseDto;
import ru.practice.recipe_service.service.entity.RecipeEntityService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class RecipeServiceImpl implements RecipeService, ConsumerProcessor {
    private final RecipeMapper recipeMapper;
    private final RecipeEntityService recipeEntityService;

    @Override
    public RecipeResponseDto findRecipe(String name) {
        var recipe = recipeEntityService.findRecipeByName(name).orElseThrow(EntityNotFoundException::new);
        return recipeMapper.toRecipeResponseDto(recipe);
    }

    @Override
    @Transactional
    public void saveRecipe(RecipeRestRequestDto recipeDto) {
        var recipe = recipeMapper.fromRecipeRestRequestDto(recipeDto);
        recipeEntityService.trySaveRecipe(recipe);
    }

    @Override
    @Transactional
    public void saveFromKafka(List<RecipeKafkaDto> recipes) {

    }
}
