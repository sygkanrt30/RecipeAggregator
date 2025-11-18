package ru.practice.recipe_aggregator.recipe_management.search_service.filtering;

import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practice.recipe_aggregator.recipe_management.model.dto.container.FilterCondition;
import ru.practice.recipe_aggregator.recipe_management.model.dto.container.FilterOperator;
import ru.practice.recipe_aggregator.recipe_management.model.dto.container.SearchContainer;
import ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering.FilterServiceImpl;
import ru.practice.shared.dto.RecipeDto;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FilterServiceImplTest {

    private FilterServiceImpl filterService;

    @BeforeEach
    void setUp() {
        filterService = new FilterServiceImpl();
    }

    @Test
    void processWithFilterChain_WithValidRecipes_ShouldFilterCorrectly() {
        var recipes = new ArrayList<RecipeDto>();
        var searchContainer = SearchContainer.builder()
                .cookingTimeCondition(new FilterCondition("cookingTime", FilterOperator.LTE, 25))
                .preparationTimeCondition(new FilterCondition("preparationTime", FilterOperator.LTE, 15))
                .totalTimeCondition(new FilterCondition("totalTime", FilterOperator.LTE, 40))
                .servingsCondition(new FilterCondition("servings", FilterOperator.GTE, 3))
                .build();

        recipes.add(createRecipe(20, 10, 30, 4));
        recipes.add(createRecipe(30, 5, 35, 2));
        recipes.add(createRecipe(15, 20, 35, 4));
        recipes.add(createRecipe(40, 10, 50, 4));

        List<RecipeDto> result = filterService.processWithFilterChain(recipes, searchContainer);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(4, result.getFirst().servings());
    }

    @Test
    void processWithFilterChain_WithNoFiltersApplied_ShouldReturnAllRecipes() {
        var recipes = new ArrayList<RecipeDto>();
        var searchContainer = SearchContainer.builder()
                .cookingTimeCondition(null)
                .preparationTimeCondition(null)
                .totalTimeCondition(null)
                .servingsCondition(null)
                .build();

        recipes.add(createRecipe(30, 15, 45, 4));
        recipes.add(createRecipe(20, 10, 30, 2));
        recipes.add(createRecipe(25, 5, 30, 6));

        List<RecipeDto> result = filterService.processWithFilterChain(recipes, searchContainer);

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    private RecipeDto createRecipe(int cookMins, int prepMins, int totalTime, int servings) {
        return Instancio.of(RecipeDto.class)
                .set(Select.field(RecipeDto::timeForCooking), Duration.ofMinutes(cookMins))
                .set(Select.field(RecipeDto::timeForPreparing), Duration.ofMinutes(prepMins))
                .set(Select.field(RecipeDto::totalTime), Duration.ofMinutes(totalTime))
                .set(Select.field(RecipeDto::servings), servings)
                .set(Select.field(RecipeDto::id), UUID.randomUUID())
                .generate(Select.field(RecipeDto::name), gen -> gen.string().length(5, 15))
                .generate(Select.field(RecipeDto::ingredients), gen -> gen.collection().size(2))
                .generate(Select.field(RecipeDto::direction), gen -> gen.string().length(20, 100))
                .generate(Select.field(RecipeDto::description), gen -> gen.string().length(10, 50))
                .set(Select.field(RecipeDto::additionalTime), Duration.ofMinutes(5))
                .create();
    }
}
