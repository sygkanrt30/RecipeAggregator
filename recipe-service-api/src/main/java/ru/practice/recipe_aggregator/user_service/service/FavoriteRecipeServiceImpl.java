package ru.practice.recipe_aggregator.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practice.recipe_aggregator.recipe_management.recipe_service.RecipeService;
import ru.practice.recipe_aggregator.user_service.repository.UserRepository;
import ru.practice.shared.dto.RecipeDto;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavoriteRecipeServiceImpl implements FavoriteRecipeService {

    private final GetUserInfoService userInfoService;
    private final UserRepository userRepository;
    private final RecipeService recipeService;

    @Override
    public void add2Favorites(String username, String recipeName) {
        UUID recipeId = recipeService.getIdByName(recipeName);
        var user = userInfoService.getUserByName(username);
        if (user.getFavoriteRecipeIds().contains(recipeId)) {
            log.warn("User {} already has favorite recipe {}", username, recipeId);
            return;
        }
        user.getFavoriteRecipeIds().add(recipeId);
        userRepository.save(user);
        log.info("Add recipe {} to favorite recipes {}", username, recipeId);
    }

    @Override
    public void removeFromFavorites(String username, String recipeName) {
        UUID recipeId = recipeService.getIdByName(recipeName);
        var user = userInfoService.getUserByName(username);
        boolean isRemoved = user.getFavoriteRecipeIds().remove(recipeId);
        if (!isRemoved) {
            log.warn("Recipe not contains in favorite recipes {}", recipeId);
            return;
        }
        userRepository.save(user);
        log.info("Remove recipe {} from favorite recipes {}", username, recipeId);
    }

    @Override
    public List<RecipeDto> getFavorites(String username) {
        var favoriteRecipeIds = userInfoService.getUserByName(username).getFavoriteRecipeIds();
        return recipeService.findAllByIds(favoriteRecipeIds);
    }
}
