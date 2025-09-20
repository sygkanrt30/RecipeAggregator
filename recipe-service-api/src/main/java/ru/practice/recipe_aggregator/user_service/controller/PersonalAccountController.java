package ru.practice.recipe_aggregator.user_service.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.practice.recipe_aggregator.recipe_management.model.dto.response.RecipeResponseDto;

import java.util.List;

@RestController
@RequestMapping("/account")
class PersonalAccountController {

    @PostMapping("/add-to-favorites")
    public void add2Favorites(@AuthenticationPrincipal UserDetails user) {

    }

    @DeleteMapping("/remove-from-favorites")
    public void removeFromFavorites(@AuthenticationPrincipal UserDetails user) {

    }

    @GetMapping("/add-to-favorites")
    public List<RecipeResponseDto> getFavorites(@AuthenticationPrincipal UserDetails user) {
        return List.of(
                new RecipeResponseDto("dawdawd", 0,0,0,0,0,null, "dadwd", "dawdawd")
        );
    }
}
