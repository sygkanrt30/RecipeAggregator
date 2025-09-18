package ru.practice.user_service.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.practice.user_service.dto.RecipeDto;

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
    public List<RecipeDto> getFavorites(@AuthenticationPrincipal UserDetails user) {
        return null;
    }
}
