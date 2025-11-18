package ru.practice.recipe_aggregator.recipe_management.search_service.filtering.filter;

import ru.practice.shared.dto.IngredientDto;
import ru.practice.shared.dto.RecipeDto;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

final class RecipesForFilterTestFactory {

    private static final Duration DEFAULT_PREP_TIME = Duration.ofMinutes(10);
    private static final Duration DEFAULT_COOKING_TIME = Duration.ofMinutes(20);
    private static final Duration DEFAULT_ADDITIONAL_TIME = Duration.ofMinutes(5);
    private static final int DEFAULT_SERVINGS = 4;
    private static final Duration ZERO_TIME = Duration.ofMinutes(0);

    static List<RecipeDto> createCookingTimeRecipes() {
        List<Duration> cookingTimes = List.of(
                Duration.ofMinutes(5), Duration.ofMinutes(25), Duration.ofMinutes(12),
                Duration.ofMinutes(15), Duration.ofMinutes(20), Duration.ofMinutes(25),
                Duration.ofMinutes(40), Duration.ofMinutes(90), Duration.ofMinutes(180),
                ZERO_TIME, ZERO_TIME, ZERO_TIME
        );

        List<RecipeDto> recipes = new ArrayList<>();
        for (int i = 0; i < cookingTimes.size(); i++) {
            Duration cookingTime = cookingTimes.get(i);
            recipes.add(createRecipe(
                    "Recipe_" + (i + 1),
                    cookingTime,
                    DEFAULT_PREP_TIME,
                    DEFAULT_ADDITIONAL_TIME,
                    DEFAULT_SERVINGS
            ));
        }
        return recipes;
    }

    static List<RecipeDto> createPrepTimeRecipes() {
        List<Duration> prepTimes = List.of(
                Duration.ofMinutes(3), Duration.ofMinutes(5), Duration.ofMinutes(10),
                Duration.ofMinutes(15), Duration.ofMinutes(25), Duration.ofMinutes(30),
                ZERO_TIME
        );

        List<RecipeDto> recipes = new ArrayList<>();
        for (int i = 0; i < prepTimes.size(); i++) {
            Duration prepTime = prepTimes.get(i);
            recipes.add(createRecipe(
                    "Prep_Recipe_" + (i + 1),
                    DEFAULT_COOKING_TIME,
                    prepTime,
                    DEFAULT_ADDITIONAL_TIME,
                    DEFAULT_SERVINGS
            ));
        }
        return recipes;
    }

    static List<RecipeDto> createTotalTimeRecipes() {
        return new ArrayList<>(List.of(
                createRecipeWithTotalTime("5-min Snack", Duration.ofMinutes(5)),
                createRecipeWithTotalTime("Quick Breakfast", Duration.ofMinutes(12)),
                createRecipeWithTotalTime("Fast Lunch", Duration.ofMinutes(20)),
                createRecipeWithTotalTime("Standard Dinner", Duration.ofMinutes(45)),
                createRecipeWithTotalTime("Weekend Meal", Duration.ofMinutes(75)),
                createRecipeWithTotalTime("Holiday Feast", Duration.ofMinutes(180)),
                createRecipeWithTotalTime("Instant Food", Duration.ofMinutes(2)),
                createRecipeWithTotalTime("All Day Cooking", Duration.ofMinutes(330))
        ));
    }

    static List<RecipeDto> createServingsRecipes() {
        List<Integer> servings = List.of(1, 2, 4, 6, 8, 12, 20, 0);

        List<RecipeDto> recipes = new ArrayList<>();
        for (int i = 0; i < servings.size(); i++) {
            int serving = servings.get(i);
            recipes.add(createRecipe(
                    "Servings_Recipe_" + (i + 1),
                    DEFAULT_COOKING_TIME,
                    DEFAULT_PREP_TIME,
                    DEFAULT_ADDITIONAL_TIME,
                    serving
            ));
        }
        return recipes;
    }

    static List<RecipeDto> createEdgeCasesRecipes() {
        return new ArrayList<>(List.of(
                createRecipe("No Time Recipe", ZERO_TIME, ZERO_TIME, ZERO_TIME, 1),
                createRecipe("Marinated Dish", Duration.ofMinutes(480), Duration.ofMinutes(60), Duration.ofMinutes(30), 4),
                createRecipe("Instant Recipe", Duration.ofMinutes(1), Duration.ofMinutes(1), ZERO_TIME, 1)
        ));
    }

    private static RecipeDto createRecipe(String name, Duration cookingTime,
                                          Duration prepTime, Duration additionalTime, int servings) {
        Duration totalMins = prepTime.plus(cookingTime).plus(additionalTime);
        List<IngredientDto> ingredients = createDefaultIngredients();

        return new RecipeDto(
                UUID.randomUUID(),
                name,
                prepTime,
                cookingTime,
                additionalTime,
                totalMins,
                servings,
                ingredients,
                createDirection(name, cookingTime),
                createDescription(name, servings)
        );
    }

    private static RecipeDto createRecipeWithTotalTime(String name, Duration totalTime) {
        Duration prepTime = totalTime.dividedBy(4);
        Duration cookingTime = totalTime.dividedBy(2);
        Duration additionalTime = totalTime.minus(prepTime).minus(cookingTime);

        return createRecipe(name, cookingTime, prepTime, additionalTime, DEFAULT_SERVINGS);
    }

    private static List<IngredientDto> createDefaultIngredients() {
        return new ArrayList<>(List.of(
                IngredientDto.of("Main Ingredient", "200", "g"),
                IngredientDto.of("Secondary Ingredient", "1", "cup"),
                IngredientDto.of("Spice", "1", "tsp")
        ));
    }

    private static String createDirection(String recipeName, Duration cookingTime) {
        return String.format("1. Prepare ingredients for %s. 2. Cook for %d minutes. 3. Serve hot.",
                recipeName, cookingTime.toMinutes());
    }

    private static String createDescription(String recipeName, int servings) {
        return String.format("A delicious %s recipe that serves %d people. Perfect for any occasion.",
                recipeName.toLowerCase(), servings);
    }
}