package ru.practice.recipe_aggregator.recipe_management.search_service.filtering.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.practice.recipe_aggregator.recipe_management.model.dto.container.FilterCondition;
import ru.practice.recipe_aggregator.recipe_management.model.dto.container.FilterOperator;
import ru.practice.recipe_aggregator.recipe_management.model.dto.container.SearchContainer;
import ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering.filter.ServingsFilter;
import ru.practice.shared.dto.RecipeDto;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServingsFilterTest {

    private ServingsFilter filter;
    private List<RecipeDto> recipes;

    @BeforeEach
    void setUp() {
        filter = new ServingsFilter();
        recipes = RecipesForFilterTestFactory.createServingsRecipes();
    }

    @Test
    void filter_WhenFilterConditionIsNull_ShouldDoNothing() {
        var searchContainer = SearchContainer.builder()
                .servingsCondition(null)
                .build();
        var recipesSizeBefore = recipes.size();

        filter.filter(recipes, searchContainer);

        assertEquals(recipesSizeBefore, recipes.size());
    }

    @Test
    void filter_WhenFilterConditionHasValueZero_ShouldDoNothing() {
        var filterCondition = new FilterCondition("servings", FilterOperator.EQ, 0);
        var searchContainer = SearchContainer.builder()
                .servingsCondition(filterCondition)
                .build();
        var recipesSizeBefore = recipes.size();

        filter.filter(recipes, searchContainer);

        assertEquals(recipesSizeBefore, recipes.size());
    }

    @Test
    void filter_WhenFilterConditionHasNegativeValue_ShouldDoNothing() {
        var filterCondition = new FilterCondition("servings", FilterOperator.EQ, -5);
        var searchContainer = SearchContainer.builder()
                .servingsCondition(filterCondition)
                .build();
        var recipesSizeBefore = recipes.size();

        filter.filter(recipes, searchContainer);

        assertEquals(recipesSizeBefore, recipes.size());
    }

    @Test
    void filter_WhenFilterOperationIsEQ_ShouldFilterRecipes() {
        var filterCondition = new FilterCondition("servings", FilterOperator.EQ, 4);
        var searchContainer = SearchContainer.builder()
                .servingsCondition(filterCondition)
                .build();
        var recipesSizeBefore = 1;

        filter.filter(recipes, searchContainer);

        assertEquals(recipesSizeBefore, recipes.size());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 6})
    void filter_WhenFilterOperationIsNEQ_ShouldFilterRecipes(int value) {
        var filterCondition = new FilterCondition("servings", FilterOperator.NEQ, value);
        var searchContainer = SearchContainer.builder()
                .servingsCondition(filterCondition)
                .build();
        var recipesSizeBefore = 7;

        filter.filter(recipes, searchContainer);

        assertEquals(recipesSizeBefore, recipes.size());
    }

    @Test
    void filter_WhenFilterOperationIsGT_ShouldFilterRecipes() {
        var filterCondition = new FilterCondition("servings", FilterOperator.GT, 8);
        var searchContainer = SearchContainer.builder()
                .servingsCondition(filterCondition)
                .build();
        var recipesSizeBefore = 2;

        filter.filter(recipes, searchContainer);

        assertEquals(recipesSizeBefore, recipes.size());
    }

    @Test
    void filter_WhenFilterOperationIsLT_ShouldFilterRecipes() {
        var filterCondition = new FilterCondition("servings", FilterOperator.LT, 4);
        var searchContainer = SearchContainer.builder()
                .servingsCondition(filterCondition)
                .build();
        var recipesSizeBefore = 3;

        filter.filter(recipes, searchContainer);

        assertEquals(recipesSizeBefore, recipes.size());
    }

    @Test
    void filter_WhenFilterOperationIsGTE_ShouldFilterRecipes() {
        var filterCondition = new FilterCondition("servings", FilterOperator.GTE, 8);
        var searchContainer = SearchContainer.builder()
                .servingsCondition(filterCondition)
                .build();
        var recipesSizeBefore = 3;

        filter.filter(recipes, searchContainer);

        assertEquals(recipesSizeBefore, recipes.size());
    }

    @Test
    void filter_WhenFilterOperationIsLTE_ShouldFilterRecipes() {
        var filterCondition = new FilterCondition("servings", FilterOperator.LTE, 2);
        var searchContainer = SearchContainer.builder()
                .servingsCondition(filterCondition)
                .build();
        var recipesSizeBefore = 3;

        filter.filter(recipes, searchContainer);

        assertEquals(recipesSizeBefore, recipes.size());
    }

    @Test
    void filter_WhenEmptyRecipeList_ShouldDoNothing() {
        var emptyRecipes = new ArrayList<RecipeDto>();
        var filterCondition = new FilterCondition("servings", FilterOperator.EQ, 4);
        var searchContainer = SearchContainer.builder()
                .servingsCondition(filterCondition)
                .build();

        filter.filter(emptyRecipes, searchContainer);

        assertTrue(emptyRecipes.isEmpty());
    }

    @Test
    void filter_WhenAllRecipesMatchCondition_ShouldReturnAll() {
        var filterCondition = new FilterCondition("servings", FilterOperator.GT, 0);
        var searchContainer = SearchContainer.builder()
                .servingsCondition(filterCondition)
                .build();
        var recipesSizeBefore = recipes.size();

        filter.filter(recipes, searchContainer);

        assertEquals(recipesSizeBefore, recipes.size());
    }

    @Test
    void filter_WhenNoRecipesMatchCondition_ShouldReturnEmptyList() {
        var filterCondition = new FilterCondition("servings", FilterOperator.GT, 50);
        var searchContainer = SearchContainer.builder()
                .servingsCondition(filterCondition)
                .build();

        filter.filter(recipes, searchContainer);

        assertTrue(recipes.isEmpty());
    }

    @Test
    void filter_WhenZeroServingsRecipes_ShouldBeFilteredOut() {
        var filterCondition = new FilterCondition("servings", FilterOperator.GT, 0);
        var searchContainer = SearchContainer.builder()
                .servingsCondition(filterCondition)
                .build();

        filter.filter(recipes, searchContainer);

        assertTrue(recipes.stream().anyMatch(recipe -> recipe.servings() == 0));
    }

    @Test
    void filter_WhenMultipleOperations_ShouldWorkCorrectly() {
        var recipesCopy = new ArrayList<>(recipes);

        var filterCondition1 = new FilterCondition("servings", FilterOperator.GT, 4);
        var searchContainer1 = SearchContainer.builder()
                .servingsCondition(filterCondition1)
                .build();

        filter.filter(recipesCopy, searchContainer1);
        assertEquals(4, recipesCopy.size());

        var filterCondition2 = new FilterCondition("servings", FilterOperator.LTE, 12);
        var searchContainer2 = SearchContainer.builder()
                .servingsCondition(filterCondition2)
                .build();

        filter.filter(recipesCopy, searchContainer2);
        assertEquals(3, recipesCopy.size());
    }
}
