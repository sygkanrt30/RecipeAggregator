package ru.practice.recipe_aggregator.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.practice.recipe_aggregator.user_service.service.FavoriteRecipeService;
import ru.practice.shared.dto.RecipeDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/account/favorite")
class PersonalAccountController {

    private final FavoriteRecipeService favoriteRecipeService;

    @PostMapping
    public void add2Favorites(@AuthenticationPrincipal UserDetails user,
                              @RequestParam(name = "recipe_name") String recipeName) {
        favoriteRecipeService.add2Favorites(user.getUsername(), recipeName);
    }

    @DeleteMapping
    public void removeFromFavorites(@AuthenticationPrincipal UserDetails user,
                                    @RequestParam(name = "recipe_name") String recipeName) {
        favoriteRecipeService.removeFromFavorites(user.getUsername(), recipeName);
    }

    @GetMapping
    public List<RecipeDto> getFavorites(@AuthenticationPrincipal UserDetails user) {
        return favoriteRecipeService.getFavorites(user.getUsername());
    }
}
