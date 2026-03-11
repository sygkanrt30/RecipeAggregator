package ru.practice.recipe_aggregator.recipe_management.search_service.filtering.filter;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.practice.recipe_aggregator.recipe_management.model.dto.container.FilterCondition;
import ru.practice.recipe_aggregator.recipe_management.model.dto.container.FilterOperator;
import ru.practice.recipe_aggregator.recipe_management.model.dto.container.SearchContainer;
import ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering.filter.CookingTimeFilter;
import ru.practice.shared.dto.RecipeDto;

import java.util.List;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CookingTimeFilterTest {

    private CookingTimeFilter filter;
    private List<RecipeDto> recipes;

    @BeforeEach
    void setUp() {
        filter = new CookingTimeFilter();
        recipes = RecipesForFilterTestFactory.createCookingTimeRecipes();
    }


    @Test
    void filter_WhenFilterConditionHasValueZero_ShouldDoNothing() {
        var filterCondition = new FilterCondition("ccookingTime", FilterOperator.EQ, 0);
        var searchContainer = Instancio.of(SearchContainer.class)
                .set(field(SearchContainer::cookingTimeCondition), filterCondition)
                .create();
        var recipesSizeBefore = recipes.size();

        filter.filter(recipes, searchContainer);

        assertEquals(recipesSizeBefore, recipes.size());

    }

    @Test
    void filter_WhenFilterConditionIsNull_ShouldDoNothing() {
        var searchContainer = Instancio.of(SearchContainer.class)
                .set(field(SearchContainer::cookingTimeCondition), null)
                .create();
        var recipesSizeBefore = recipes.size();

        filter.filter(recipes, searchContainer);

        assertEquals(recipesSizeBefore, recipes.size());

    }

    @Test
    void filter_WhenFilterOperationIsEQ_ShouldFilterRecipes() {
        var filterCondition = new FilterCondition("cookingTime", FilterOperator.EQ, 25);
        var searchContainer = Instancio.of(SearchContainer.class)
                .set(field(SearchContainer::cookingTimeCondition), filterCondition)
                .create();
        var recipesSizeBefore = 5;

        filter.filter(recipes, searchContainer);

        assertEquals(recipesSizeBefore, recipes.size());
    }

    @ParameterizedTest
    @ValueSource(ints = {12, 90, 40})
    void filter_WhenFilterOperationIsNEQ_ShouldFilterRecipes(int value) {
        var filterCondition = new FilterCondition("cookingTime", FilterOperator.NEQ, value);
        var searchContainer = Instancio.of(SearchContainer.class)
                .set(field(SearchContainer::cookingTimeCondition), filterCondition)
                .create();
        var recipesSizeBefore = 11;

        filter.filter(recipes, searchContainer);

        assertEquals(recipesSizeBefore, recipes.size());
    }

    @Test
    void filter_WhenFilterOperationIsGT_ShouldFilterRecipes() {
        var filterCondition = new FilterCondition("cookingTime", FilterOperator.GT, 25);
        var searchContainer = Instancio.of(SearchContainer.class)
                .set(field(SearchContainer::cookingTimeCondition), filterCondition)
                .create();
        var recipesSizeBefore = 6;

        filter.filter(recipes, searchContainer);

        assertEquals(recipesSizeBefore, recipes.size());
    }

    @Test
    void filter_WhenFilterOperationIsLT_ShouldFilterRecipes() {
        var filterCondition = new FilterCondition("cookingTime", FilterOperator.LT, 60);
        var searchContainer = Instancio.of(SearchContainer.class)
                .set(field(SearchContainer::cookingTimeCondition), filterCondition)
                .create();
        var recipesSizeBefore = 10;

        filter.filter(recipes, searchContainer);

        assertEquals(recipesSizeBefore, recipes.size());
    }

    @Test
    void filter_WhenFilterOperationIsGTE_ShouldFilterRecipes() {
        var filterCondition = new FilterCondition("cookingTime", FilterOperator.GTE, 90);
        var searchContainer = Instancio.of(SearchContainer.class)
                .set(field(SearchContainer::cookingTimeCondition), filterCondition)
                .create();
        var recipesSizeBefore = 5;

        filter.filter(recipes, searchContainer);

        assertEquals(recipesSizeBefore, recipes.size());
    }

    @Test
    void filter_WhenFilterOperationIsLTE_ShouldFilterRecipes() {
        var filterCondition = new FilterCondition("cookingTime", FilterOperator.LTE, 5);
        var searchContainer = Instancio.of(SearchContainer.class)
                .set(field(SearchContainer::cookingTimeCondition), filterCondition)
                .create();
        var recipesSizeBefore = 4;

        filter.filter(recipes, searchContainer);

        assertEquals(recipesSizeBefore, recipes.size());
    }

    @Test
    void filter_WhenFilterEdgeCasesOperationIsGTE_ShouldFilterRecipes() {
        var edgeRecipes = RecipesForFilterTestFactory.createEdgeCasesRecipes();
        var filterCondition = new FilterCondition("cookingTime", FilterOperator.GTE, 50);
        var searchContainer = Instancio.of(SearchContainer.class)
                .set(field(SearchContainer::cookingTimeCondition), filterCondition)
                .create();
        var recipesSizeBefore = 2;

        filter.filter(edgeRecipes, searchContainer);

        assertEquals(recipesSizeBefore, edgeRecipes.size());
    }
}
