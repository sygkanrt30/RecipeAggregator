package ru.practice.recipe_aggregator.user_service.service;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.practice.recipe_aggregator.recipe_management.model.dto.response.RecipeResponseDto;
import ru.practice.recipe_aggregator.recipe_management.recipe_service.RecipeService;
import ru.practice.recipe_aggregator.user_service.model.User;
import ru.practice.recipe_aggregator.user_service.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RecipeService recipeService;

    @InjectMocks
    private UserService userService;

    @Test
    void getFavorites_shouldReturnFavoriteRecipesForUser_whenUsernameCorrect() {
        var user = Instancio.create(User.class);
        var favoriteRecipes = user.getFavoriteRecipeIds();
        var expectedRecipes = Instancio.ofList(RecipeResponseDto.class)
                .size(favoriteRecipes.size())
                .create();
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(recipeService.findAllByIds(favoriteRecipes)).thenReturn(expectedRecipes);

        List<RecipeResponseDto> result = userService.getFavorites("any");

        assertDoesNotThrow(() -> userService.getFavorites("any"));
        assertNotNull(result);
        assertEquals(expectedRecipes.size(), result.size());
    }

    @Test
    void getFavorites_shouldThrowException_whenUsernameNotCorrect() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.getFavorites("any"));
    }

    @Test
    void add2Favorites_shouldAddRecipeIdToFavorites_RecipeNotAlreadyAdded() {
        var user = Instancio.create(User.class);
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));

        userService.add2Favorites("any", UUID.randomUUID().toString());

        verify(userRepository).save(user);
    }

    @Test
    void add2Favorites_shouldOnlyLog_RecipeAlreadyAdded() {
        var user = Instancio.create(User.class);
        var favoriteRecipes = user.getFavoriteRecipeIds();
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(recipeService.getIdByName(any())).thenReturn(favoriteRecipes.getFirst());

        userService.add2Favorites("any", "any");

        verify(userRepository, never()).save(user);
    }

    @Test
    void removeFromFavorites_shouldRemoveRecipeIdFromFavorites_RecipeExist() {
        var user = Instancio.create(User.class);
        var favoriteRecipes = user.getFavoriteRecipeIds();
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(recipeService.getIdByName(any())).thenReturn(favoriteRecipes.getFirst());

        userService.removeFromFavorites("any", "any");

        verify(userRepository).save(user);
    }

    @Test
    void removeFromFavorites_shouldThrowException_RecipeNotExist() {
        var user = Instancio.create(User.class);
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        var returnedRecipe = UUID.randomUUID();
        when(recipeService.getIdByName(any())).thenReturn(returnedRecipe);

        var exception = assertThrows(RecipeNotContainsException.class,
                () -> userService.removeFromFavorites("any", "any"));
        assertEquals("Recipe not contains in favorite recipes " + returnedRecipe, exception.getMessage());
    }
}
