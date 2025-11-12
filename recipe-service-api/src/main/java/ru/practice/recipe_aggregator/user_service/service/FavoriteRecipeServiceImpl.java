package ru.practice.recipe_aggregator.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practice.recipe_aggregator.recipe_management.recipe_service.RecipeService;
import ru.practice.recipe_aggregator.user_service.model.FavoriteRecipe;
import ru.practice.recipe_aggregator.user_service.model.FavoriteRecipeId;
import ru.practice.recipe_aggregator.user_service.repository.FavoriteRecipeRepository;
import ru.practice.shared.dto.RecipeDto;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavoriteRecipeServiceImpl implements FavoriteRecipeService {

    private final GetUserInfoService userInfoService;
    private final RecipeService recipeService;
    private final FavoriteRecipeRepository favoriteRecipeRepository;

    @Override
    public void add2Favorites(String username, String recipeName) {
        UUID recipeId = recipeService.getIdByName(recipeName);
        var user = userInfoService.getUserByName(username);
        List<UUID> recipeIds = user.getFavoriteRecipes().stream()
                .map(FavoriteRecipe::getId)
                .map(FavoriteRecipeId::getRecipeId)
                .toList();
        if (recipeIds.contains(recipeId)) {
            log.warn("User {} already has favorite recipe {}", username, recipeId);
            return;
        }
        var favoriteRecipeId = new FavoriteRecipeId(user.getId(), recipeId);
        var favoriteRecipe = new FavoriteRecipe(favoriteRecipeId, user);
        favoriteRecipeRepository.save(favoriteRecipe);
        log.info("Add recipe {} to favorite recipes {}", username, recipeId);
    }

    @Override
    public void removeFromFavorites(String username, String recipeName) {
        UUID recipeId = recipeService.getIdByName(recipeName);
        var user = userInfoService.getUserByName(username);
        favoriteRecipeRepository.deleteById(new FavoriteRecipeId(user.getId(), recipeId));
        log.info("Remove recipe {} from favorite recipes {}", username, recipeId);
    }

    @Override
    public List<RecipeDto> getFavorites(String username) {
        List<FavoriteRecipe> favoriteRecipeIds = userInfoService.getUserByName(username).getFavoriteRecipes();
        return recipeService.findAllByIds(favoriteRecipeIds.stream()
                .map(FavoriteRecipe::getId)
                .map(FavoriteRecipeId::getRecipeId)
                .collect(Collectors.toList()));
    }
}
