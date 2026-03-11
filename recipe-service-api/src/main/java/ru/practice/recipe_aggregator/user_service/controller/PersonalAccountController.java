package ru.practice.recipe_aggregator.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.practice.recipe_aggregator.translator.TranslatorUtil;
import ru.practice.recipe_aggregator.user_service.service.FavoriteRecipeService;
import ru.practice.shared.dto.RecipeDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/account/favorite")
class PersonalAccountController {

    private final FavoriteRecipeService favoriteRecipeService;
    private final TranslatorUtil translator;

    @PostMapping
    public void add2Favorites(@AuthenticationPrincipal UserDetails user,
                              @RequestParam(name = "recipe_name") String recipeName) {
        String nameOnEN = translator.translateTextDependingOnWebsiteLanguage(recipeName);
        favoriteRecipeService.add2Favorites(user.getUsername(), nameOnEN);
    }

    @DeleteMapping
    public void removeFromFavorites(@AuthenticationPrincipal UserDetails user,
                                    @RequestParam(name = "recipe_name") String recipeName) {
        String nameOnEN = translator.translateTextDependingOnWebsiteLanguage(recipeName);
        favoriteRecipeService.removeFromFavorites(user.getUsername(), nameOnEN);
    }

    @GetMapping
    public List<RecipeDto> getFavorites(@AuthenticationPrincipal UserDetails user,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "15") int size) {
        List<RecipeDto> resultOnEN = favoriteRecipeService.getFavorites(user.getUsername(), page, size);
        return translator.translateDtoDependingOnWebsiteLanguage(resultOnEN);
    }

}
