package ru.practice.recipe_aggregator.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.practice.recipe_aggregator.recipe_management.model.dto.response.RecipeResponseDto;
import ru.practice.recipe_aggregator.user_service.service.FavoriteRecipeService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
class PersonalAccountController {
    private final FavoriteRecipeService favoriteRecipeService;

    @PostMapping("/add-to-favorites")
    public void add2Favorites(@AuthenticationPrincipal UserDetails user,
                              @RequestParam(name = "recipe_id") UUID recipeId) {
        favoriteRecipeService.add2Favorites(user.getUsername(), recipeId);
    }

    @DeleteMapping("/remove-from-favorites")
    public void removeFromFavorites(@AuthenticationPrincipal UserDetails user,
                                    @RequestParam(name = "recipe_id") UUID recipeId) {
        favoriteRecipeService.removeFromFavorites(user.getUsername(), recipeId);
    }

    @GetMapping("/add-to-favorites")
    public List<RecipeResponseDto> getFavorites(@AuthenticationPrincipal UserDetails user) {
        return favoriteRecipeService.getFavorites(user.getUsername());
    }
}
