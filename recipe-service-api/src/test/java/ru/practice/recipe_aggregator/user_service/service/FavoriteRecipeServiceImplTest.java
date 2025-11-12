package ru.practice.recipe_aggregator.user_service.service;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practice.recipe_aggregator.recipe_management.recipe_service.RecipeService;
import ru.practice.recipe_aggregator.user_service.model.FavoriteRecipe;
import ru.practice.recipe_aggregator.user_service.model.FavoriteRecipeId;
import ru.practice.recipe_aggregator.user_service.model.User;
import ru.practice.recipe_aggregator.user_service.repository.FavoriteRecipeRepository;
import ru.practice.shared.dto.RecipeDto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteRecipeServiceImplTest {
    @Mock
    private FavoriteRecipeRepository favoriteRecipeRepository;

    @Mock
    private RecipeService recipeService;

    @Mock
    private GetUserInfoService userInfoService;

    @InjectMocks
    private FavoriteRecipeServiceImpl favoriteService;

    @Test
    void getFavorites_shouldReturnFavoriteRecipesForUser_whenUsernameCorrect() {
        // given
        var user = Instancio.create(User.class);
        var favoriteRecipes = List.of(
                new FavoriteRecipe(new FavoriteRecipeId(user.getId(), UUID.randomUUID()), user),
                new FavoriteRecipe(new FavoriteRecipeId(user.getId(), UUID.randomUUID()), user)
        );
        user.setFavoriteRecipes(favoriteRecipes);

        var expectedRecipes = Instancio.ofList(RecipeDto.class)
                .size(favoriteRecipes.size())
                .create();

        when(userInfoService.getUserByName(any())).thenReturn(user);
        when(recipeService.findAllByIds(any())).thenReturn(expectedRecipes);

        // when
        List<RecipeDto> result = favoriteService.getFavorites("any");

        // then
        assertNotNull(result);
        assertEquals(expectedRecipes.size(), result.size());
        verify(recipeService).findAllByIds(argThat(ids ->
                ids.size() == favoriteRecipes.size() &&
                        ids.containsAll(favoriteRecipes.stream()
                                .map(fav -> fav.getId().getRecipeId())
                                .toList())
        ));
    }

    @Test
    void add2Favorites_shouldAddRecipeIdToFavorites_RecipeNotAlreadyAdded() {
        // given
        var user = Instancio.create(User.class);
        user.setFavoriteRecipes(new ArrayList<>());
        var recipeId = UUID.randomUUID();
        var recipeName = "testRecipe";
        when(userInfoService.getUserByName(any())).thenReturn(user);
        when(recipeService.getIdByName(recipeName)).thenReturn(recipeId);

        // when
        favoriteService.add2Favorites("any", recipeName);

        // then
        verify(favoriteRecipeRepository).save(argThat(favorite ->
                favorite.getId().getUserId().equals(user.getId()) &&
                        favorite.getId().getRecipeId().equals(recipeId)
        ));
    }

    @Test
    void add2Favorites_shouldOnlyLog_RecipeAlreadyAdded() {
        // given
        var user = Instancio.create(User.class);
        var recipeId = UUID.randomUUID();
        var recipeName = "testRecipe";
        var existingFavorite = new FavoriteRecipe(new FavoriteRecipeId(user.getId(), recipeId), user);
        user.setFavoriteRecipes(List.of(existingFavorite));

        when(userInfoService.getUserByName(any())).thenReturn(user);
        when(recipeService.getIdByName(recipeName)).thenReturn(recipeId);

        // when
        favoriteService.add2Favorites("any", recipeName);

        // then
        verify(favoriteRecipeRepository, never()).save(any());
    }

    @Test
    void removeFromFavorites_shouldRemoveRecipeIdFromFavorites_RecipeExist() {
        // given
        var user = Instancio.create(User.class);
        var recipeId = UUID.randomUUID();
        var recipeName = "testRecipe";

        when(userInfoService.getUserByName(any())).thenReturn(user);
        when(recipeService.getIdByName(recipeName)).thenReturn(recipeId);

        // when
        favoriteService.removeFromFavorites("any", recipeName);

        // then
        verify(favoriteRecipeRepository).deleteById(argThat(favoriteId ->
                favoriteId.getUserId().equals(user.getId()) &&
                        favoriteId.getRecipeId().equals(recipeId)
        ));
    }

    @Test
    void removeFromFavorites_shouldWorkEvenIfRecipeNotInFavorites() {
        // given
        var user = Instancio.create(User.class);
        var recipeId = UUID.randomUUID();
        var recipeName = "testRecipe";
        user.setFavoriteRecipes(new ArrayList<>());
        when(userInfoService.getUserByName(any())).thenReturn(user);
        when(recipeService.getIdByName(recipeName)).thenReturn(recipeId);

        // when
        favoriteService.removeFromFavorites("any", recipeName);

        // then
        verify(favoriteRecipeRepository).deleteById(argThat(favoriteId ->
                favoriteId.getUserId().equals(user.getId()) &&
                        favoriteId.getRecipeId().equals(recipeId)
        ));
    }
}
