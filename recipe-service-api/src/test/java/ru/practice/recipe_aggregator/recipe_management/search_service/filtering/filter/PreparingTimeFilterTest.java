package ru.practice.recipe_aggregator.recipe_management.search_service.filtering.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.practice.recipe_aggregator.recipe_management.model.dto.container.FilterCondition;
import ru.practice.recipe_aggregator.recipe_management.model.dto.container.FilterOperator;
import ru.practice.recipe_aggregator.recipe_management.model.dto.container.SearchContainer;
import ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering.filter.PreparingTimeFilter;
import ru.practice.shared.dto.RecipeDto;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PreparingTimeFilterTest {

    private PreparingTimeFilter filter;
    private List<RecipeDto> recipes;

    @BeforeEach
    void setUp() {
        filter = new PreparingTimeFilter();
        recipes = RecipesForFilterTestFactory.createPrepTimeRecipes();
    }

    @Test
    void filter_WhenFilterConditionHasValueZero_ShouldDoNothing() {
        var filterCondition = new FilterCondition("preparationTime", FilterOperator.EQ, 0);
        var searchContainer = SearchContainer.builder()
                .preparationTimeCondition(filterCondition)
                .build();
        var recipesSizeBefore = recipes.size();

        filter.filter(recipes, searchContainer);

        assertEquals(recipesSizeBefore, recipes.size());
    }

    @Test
    void filter_WhenFilterConditionIsNull_ShouldDoNothing() {
        var searchContainer = SearchContainer.builder()
                .preparationTimeCondition(null)
                .build();
        var recipesSizeBefore = recipes.size();

        filter.filter(recipes, searchContainer);

        assertEquals(recipesSizeBefore, recipes.size());
    }

    @Test
    void filter_WhenFilterConditionHasNegativeValue_ShouldDoNothing() {
        var filterCondition = new FilterCondition("preparationTime", FilterOperator.EQ, -5);
        var searchContainer = SearchContainer.builder()
                .preparationTimeCondition(filterCondition)
                .build();
        var recipesSizeBefore = recipes.size();

        filter.filter(recipes, searchContainer);

        assertEquals(recipesSizeBefore, recipes.size());
    }

    @Test
    void filter_WhenFilterOperationIsEQ_ShouldFilterRecipes() {
        var filterCondition = new FilterCondition("preparationTime", FilterOperator.EQ, 10);
        var searchContainer = SearchContainer.builder()
                .preparationTimeCondition(filterCondition)
                .build();
        var recipesSizeBefore = 2;

        filter.filter(recipes, searchContainer);

        assertEquals(recipesSizeBefore, recipes.size());
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 5, 15})
    void filter_WhenFilterOperationIsNEQ_ShouldFilterRecipes(int value) {
        var filterCondition = new FilterCondition("preparationTime", FilterOperator.NEQ, value);
        var searchContainer = SearchContainer.builder()
                .preparationTimeCondition(filterCondition)
                .build();
        var recipesSizeBefore = 6;

        filter.filter(recipes, searchContainer);

        assertEquals(recipesSizeBefore, recipes.size());
    }

    @Test
    void filter_WhenFilterOperationIsGT_ShouldFilterRecipes() {
        var filterCondition = new FilterCondition("preparationTime", FilterOperator.GT, 15);
        var searchContainer = SearchContainer.builder()
                .preparationTimeCondition(filterCondition)
                .build();
        var recipesSizeBefore = 3;

        filter.filter(recipes, searchContainer);

        assertEquals(recipesSizeBefore, recipes.size());
    }

    @Test
    void filter_WhenFilterOperationIsLT_ShouldFilterRecipes() {
        var filterCondition = new FilterCondition("preparationTime", FilterOperator.LT, 10);
        var searchContainer = SearchContainer.builder()
                .preparationTimeCondition(filterCondition)
                .build();
        var recipesSizeBefore = 3;

        filter.filter(recipes, searchContainer);

        assertEquals(recipesSizeBefore, recipes.size());
    }

    @Test
    void filter_WhenFilterOperationIsGTE_ShouldFilterRecipes() {
        var filterCondition = new FilterCondition("preparationTime", FilterOperator.GTE, 15);
        var searchContainer = SearchContainer.builder()
                .preparationTimeCondition(filterCondition)
                .build();
        var recipesSizeBefore = 4;

        filter.filter(recipes, searchContainer);

        assertEquals(recipesSizeBefore, recipes.size());
    }

    @Test
    void filter_WhenFilterOperationIsLTE_ShouldFilterRecipes() {
        var filterCondition = new FilterCondition("preparationTime", FilterOperator.LTE, 5);
        var searchContainer = SearchContainer.builder()
                .preparationTimeCondition(filterCondition)
                .build();
        var recipesSizeBefore = 3;

        filter.filter(recipes, searchContainer);

        assertEquals(recipesSizeBefore, recipes.size());
    }

    @Test
    void filter_WhenEmptyRecipeList_ShouldDoNothing() {
        var emptyRecipes = new ArrayList<RecipeDto>();
        var filterCondition = new FilterCondition("preparationTime", FilterOperator.EQ, 10);
        var searchContainer = SearchContainer.builder()
                .preparationTimeCondition(filterCondition)
                .build();

        filter.filter(emptyRecipes, searchContainer);

        assertTrue(emptyRecipes.isEmpty());
    }

    @Test
    void filter_WhenAllRecipesMatchCondition_ShouldReturnAll() {
        var filterCondition = new FilterCondition("preparationTime", FilterOperator.GTE, 0);
        var searchContainer = SearchContainer.builder()
                .preparationTimeCondition(filterCondition)
                .build();
        var recipesSizeBefore = recipes.size();

        filter.filter(recipes, searchContainer);

        assertEquals(recipesSizeBefore, recipes.size());
    }

    @Test
    void filter_WhenNoRecipesMatchCondition_ShouldReturnEmptyList() {
        var filterCondition = new FilterCondition("preparationTime", FilterOperator.GT, 50);
        var searchContainer = SearchContainer.builder()
                .preparationTimeCondition(filterCondition)
                .build();

        filter.filter(recipes, searchContainer);

        assertFalse(recipes.isEmpty());
    }

    @Test
    void filter_WhenZeroPreparationTimeRecipes_ShouldHandleCorrectly() {
        var filterCondition = new FilterCondition("preparationTime", FilterOperator.EQ, 0);
        var searchContainer = SearchContainer.builder()
                .preparationTimeCondition(filterCondition)
                .build();
        var expectedSizeBefore = recipes.size();

        filter.filter(recipes, searchContainer);

        assertEquals(expectedSizeBefore, recipes.size());
    }

    @Test
    void filter_WhenMultipleOperations_ShouldWorkCorrectly() {
        var recipesCopy = new ArrayList<>(recipes);

        var filterCondition1 = new FilterCondition("preparationTime", FilterOperator.GT, 5);
        var searchContainer1 = SearchContainer.builder()
                .preparationTimeCondition(filterCondition1)
                .build();

        filter.filter(recipesCopy, searchContainer1);
        assertEquals(5, recipesCopy.size());

        var filterCondition2 = new FilterCondition("preparationTime", FilterOperator.LTE, 15);
        var searchContainer2 = SearchContainer.builder()
                .preparationTimeCondition(filterCondition2)
                .build();

        filter.filter(recipesCopy, searchContainer2);
        assertEquals(3, recipesCopy.size());
    }
}
