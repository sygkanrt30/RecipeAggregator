package ru.practice.recipe_aggregator.search_service.service.filtering;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practice.recipe_aggregator.model.dto.container.SearchContainer;
import ru.practice.recipe_aggregator.model.dto.response.RecipeResponseDto;
import ru.practice.recipe_aggregator.search.filtering.FilterServiceImpl;
import ru.practice.recipe_aggregator.search.filtering.filter.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilterServiceImplTest {
    @Test
    void processWithFilterChain_WithRealFilters_ShouldWorkCorrectly() {
        // Arrange
        FilterServiceImpl filterService = new FilterServiceImpl();
        List<RecipeResponseDto> recipes = new ArrayList<>();
        var searchContainer = Instancio.create(SearchContainer.class);
        searchContainer.maxMins4Cook(0);
        searchContainer.maxMins4Cook(Integer.MAX_VALUE);
        searchContainer.maxTotalMins(Integer.MAX_VALUE);
        searchContainer.minServings(0);
        searchContainer.maxServings(Integer.MAX_VALUE);
        recipes.add(createRecipe(30, 15, 45, 4));
        recipes.add(createRecipe(20, 10, 30, 2));

        // Act
        List<RecipeResponseDto> result = filterService.processWithFilterChain(recipes, searchContainer);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void filterChain_ShouldContainCorrectFilters() throws Exception {
        // Arrange
        var filterService = new FilterServiceImpl();
        Field filterChainField = FilterServiceImpl.class.getDeclaredField("filterChain");
        filterChainField.setAccessible(true);
        List<Filter> filterChain = (List<Filter>) filterChainField.get(filterService);

        // Assert
        assertNotNull(filterChain);
        assertEquals(4, filterChain.size());
        assertInstanceOf(Mins4CookFilter.class, filterChain.get(0));
        assertInstanceOf(Mins4PrepFilter.class, filterChain.get(1));
        assertInstanceOf(TotalMinsFilter.class, filterChain.get(2));
        assertInstanceOf(ServingsFilter.class, filterChain.get(3));
    }

    private RecipeResponseDto createRecipe(int cookMins, int prepMins, int totalMins, int servings) {
        RecipeResponseDto recipe = mock(RecipeResponseDto.class);
        when(recipe.mins4Cook()).thenReturn(cookMins);
        when(recipe.mins4Prep()).thenReturn(prepMins);
        when(recipe.totalMins()).thenReturn(totalMins);
        when(recipe.servings()).thenReturn(servings);
        return recipe;
    }
}
