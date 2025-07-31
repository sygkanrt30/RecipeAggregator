package ru.practice.recipe_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practice.recipe_service.model.dto.factory.ResponseDtoFactory;
import ru.practice.recipe_service.model.dto.request.RecipeRestRequestDto;
import ru.practice.recipe_service.model.dto.response.RecipeResponseDto;
import ru.practice.recipe_service.model.dto.response.ResponseDto;
import ru.practice.recipe_service.service.RecipeService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/recipes")
@RequiredArgsConstructor
//служебный контроллер(все апи только для других сервисов или админов)
public class RecipeController {
    private final RecipeService recipeService;

    @GetMapping("/get/{name}")
    public ResponseEntity<RecipeResponseDto> find(@PathVariable String name) {
        RecipeResponseDto recipe =  recipeService.findRecipe(name);
        return ResponseEntity.ok(recipe);
    }

    @PostMapping("/create")
    public ResponseDto save(@RequestBody @Valid RecipeRestRequestDto recipe) {
        recipeService.saveRecipe(recipe);
        return ResponseDtoFactory.getResponseCreated();
    }

    @DeleteMapping("/delete/{username}")
    public ResponseDto delete(@PathVariable String username) {
        recipeService.deleteRecipe(username);
        return ResponseDtoFactory.getResponseOK();
    }
}
