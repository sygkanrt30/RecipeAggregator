package ru.practice.parser_service.service.cache;

import lombok.extern.slf4j.Slf4j;
import ru.practice.shared.dto.RecipeDto;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class RecipeCache implements Cache<RecipeDto> {

    private final Set<RecipeDto> parsedRecipeStorage = new HashSet<>();

    @Override
    public void put(RecipeDto recipes) {
        boolean isAlreadyContains = !parsedRecipeStorage.add(recipes);
        if (isAlreadyContains){
            log.warn("Recipe already parsed: {}", recipes);
        }
    }

    @Override
    public void putAll(Collection<RecipeDto> recipes) {
        boolean isAddNewRecipes = parsedRecipeStorage.addAll(recipes);
        if (!isAddNewRecipes){
            log.warn("Something recipes already parsed");
        }
    }

    @Override
    public boolean contains(RecipeDto recipes) {
        if (!parsedRecipeStorage.contains(recipes)) {
            put(recipes);
            return false;
        }
        return true;
    }
}
