package ru.practice.recipe_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practice.recipe_service.model.dto.factory.ResponseDtoFactory;
import ru.practice.recipe_service.model.dto.response.RecipeResponseDto;
import ru.practice.recipe_service.model.dto.response.ResponseDto;
import ru.practice.recipe_service.service.RecipeService;

@RestController
@RequestMapping("/api/v1/recipes")
@RequiredArgsConstructor
//служебный контроллер(апи только для других сервисов)
public class RecipeController {
    private final RecipeService recipeService;

    @GetMapping("/get-by-name/{name}")
    public ResponseEntity<RecipeResponseDto> findByName(@PathVariable String name) {
        RecipeResponseDto recipe =  recipeService.findRecipeByName(name);
        return ResponseEntity.ok(recipe);
    }

    @DeleteMapping("/delete/{username}")
    public ResponseDto delete(@PathVariable String username) {
        recipeService.deleteRecipeByName(username);
        return ResponseDtoFactory.getResponseOK();
    }
}
