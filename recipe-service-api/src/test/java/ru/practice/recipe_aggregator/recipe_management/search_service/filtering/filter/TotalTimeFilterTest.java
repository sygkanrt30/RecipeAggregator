package ru.practice.recipe_aggregator.recipe_management.search_service.filtering.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.practice.recipe_aggregator.recipe_management.model.dto.container.FilterCondition;
import ru.practice.recipe_aggregator.recipe_management.model.dto.container.FilterOperator;
import ru.practice.recipe_aggregator.recipe_management.model.dto.container.SearchContainer;
import ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering.filter.TotalTimeFilter;
import ru.practice.shared.dto.RecipeDto;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class TotalTimeFilterTest {

    private TotalTimeFilter filter;
    private List<RecipeDto> recipes;

    @BeforeEach
    void setUp() {
        filter = new TotalTimeFilter();
        recipes = RecipesForFilterTestFactory.createTotalTimeRecipes();
    }

    @Test
    void filter_WhenFilterConditionHasValueZero_ShouldDoNothing() {
        var filterCondition = new FilterCondition("totalTime", FilterOperator.EQ, 0);
        var searchContainer = SearchContainer.builder()
                .totalTimeCondition(filterCondition)
                .build();
        var recipesSizeBefore = recipes.size();

        filter.filter(recipes, searchContainer);

        assertEquals(recipesSizeBefore, recipes.size());
    }

    @Test
    void filter_WhenFilterConditionIsNull_ShouldDoNothing() {
        var searchContainer = SearchContainer.builder()
                .totalTimeCondition(null)
                .build();
        var recipesSizeBefore = recipes.size();

        filter.filter(recipes, searchContainer);

        assertEquals(recipesSizeBefore, recipes.size());
    }

    @Test
    void filter_WhenFilterConditionHasNegativeValue_ShouldDoNothing() {
        var filterCondition = new FilterCondition("totalTime", FilterOperator.EQ, -5);
        var searchContainer = SearchContainer.builder()
                .totalTimeCondition(filterCondition)
                .build();
        var recipesSizeBefore = recipes.size();

        filter.filter(recipes, searchContainer);

        assertEquals(recipesSizeBefore, recipes.size());
    }

    @Test
    void filter_WhenFilterOperationIsEQ_ShouldFilterRecipes() {
        var filterCondition = new FilterCondition("totalTime", FilterOperator.EQ, 45);
        var searchContainer = SearchContainer.builder()
                .totalTimeCondition(filterCondition)
                .build();
        var recipesSizeBefore = 1;

        filter.filter(recipes, searchContainer);

        assertEquals(recipesSizeBefore, recipes.size());
        assertEquals("Standard Dinner", recipes.getFirst().name());
    }

    @ParameterizedTest
    @ValueSource(ints = {12, 20, 180})
    void filter_WhenFilterOperationIsNEQ_ShouldFilterRecipes(int value) {
        var filterCondition = new FilterCondition("totalTime", FilterOperator.NEQ, value);
        var searchContainer = SearchContainer.builder()
                .totalTimeCondition(filterCondition)
                .build();
        var recipesSizeBefore = 7;

        filter.filter(recipes, searchContainer);

        assertEquals(recipesSizeBefore, recipes.size());
    }

    @Test
    void filter_WhenFilterOperationIsGT_ShouldFilterRecipes() {
        var filterCondition = new FilterCondition("totalTime", FilterOperator.GT, 75);
        var searchContainer = SearchContainer.builder()
                .totalTimeCondition(filterCondition)
                .build();
        var recipesSizeBefore = 2;
        filter.filter(recipes, searchContainer);

        assertEquals(recipesSizeBefore, recipes.size());
    }

    @Test
    void filter_WhenFilterOperationIsLT_ShouldFilterRecipes() {
        var filterCondition = new FilterCondition("totalTime", FilterOperator.LT, 20);
        var searchContainer = SearchContainer.builder()
                .totalTimeCondition(filterCondition)
                .build();
        var recipesSizeBefore = 3;

        filter.filter(recipes, searchContainer);

        assertEquals(recipesSizeBefore, recipes.size());
    }

    @Test
    void filter_WhenFilterOperationIsGTE_ShouldFilterRecipes() {
        var filterCondition = new FilterCondition("totalTime", FilterOperator.GTE, 75);
        var searchContainer = SearchContainer.builder()
                .totalTimeCondition(filterCondition)
                .build();
        var recipesSizeBefore = 3;

        filter.filter(recipes, searchContainer);

        assertEquals(recipesSizeBefore, recipes.size());
    }

    @Test
    void filter_WhenFilterOperationIsLTE_ShouldFilterRecipes() {
        var filterCondition = new FilterCondition("totalTime", FilterOperator.LTE, 12);
        var searchContainer = SearchContainer.builder()
                .totalTimeCondition(filterCondition)
                .build();
        var recipesSizeBefore = 3;

        filter.filter(recipes, searchContainer);

        assertEquals(recipesSizeBefore, recipes.size());
    }

    @Test
    void filter_WhenEmptyRecipeList_ShouldDoNothing() {
        var emptyRecipes = new ArrayList<RecipeDto>();
        var filterCondition = new FilterCondition("totalTime", FilterOperator.EQ, 45);
        var searchContainer = SearchContainer.builder()
                .totalTimeCondition(filterCondition)
                .build();

        filter.filter(emptyRecipes, searchContainer);

        assertTrue(emptyRecipes.isEmpty());
    }

    @Test
    void filter_WhenAllRecipesMatchCondition_ShouldReturnAll() {
        var filterCondition = new FilterCondition("totalTime", FilterOperator.GT, 0);
        var searchContainer = SearchContainer.builder()
                .totalTimeCondition(filterCondition)
                .build();
        var recipesSizeBefore = recipes.size();

        filter.filter(recipes, searchContainer);

        assertEquals(recipesSizeBefore, recipes.size());
    }

    @Test
    void filter_WhenNoRecipesMatchCondition_ShouldReturnEmptyList() {
        var filterCondition = new FilterCondition("totalTime", FilterOperator.GT, 500);
        var searchContainer = SearchContainer.builder()
                .totalTimeCondition(filterCondition)
                .build();

        filter.filter(recipes, searchContainer);

        assertTrue(recipes.isEmpty());
    }

    @Test
    void filter_WhenEdgeCasesOperationIsLTE_ShouldFilterRecipes() {
        var edgeRecipes = RecipesForFilterTestFactory.createEdgeCasesRecipes();
        var filterCondition = new FilterCondition("totalTime", FilterOperator.LTE, 100);
        var searchContainer = SearchContainer.builder()
                .totalTimeCondition(filterCondition)
                .build();
        var recipesSizeBefore = 2;

        filter.filter(edgeRecipes, searchContainer);

        assertEquals(recipesSizeBefore, edgeRecipes.size());
    }

    @Test
    void filter_WhenMultipleOperations_ShouldWorkCorrectly() {
        var recipesCopy = new ArrayList<>(recipes);

        var filterCondition1 = new FilterCondition("totalTime", FilterOperator.GT, 50);
        var searchContainer1 = SearchContainer.builder()
                .totalTimeCondition(filterCondition1)
                .build();

        filter.filter(recipesCopy, searchContainer1);
        assertEquals(3, recipesCopy.size());

        var filterCondition2 = new FilterCondition("totalTime", FilterOperator.LTE, 180);
        var searchContainer2 = SearchContainer.builder()
                .totalTimeCondition(filterCondition2)
                .build();

        filter.filter(recipesCopy, searchContainer2);
        assertEquals(2, recipesCopy.size());
    }
}