package ru.practice.recipe_aggregator.recipe_management.search_service.filtering;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practice.recipe_aggregator.recipe_management.model.dto.container.SearchContainer;
import ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering.FilterServiceImpl;
import ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering.filter.*;
import ru.practice.shared.dto.RecipeDto;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilterServiceImplTest {
    @Test
    void processWithFilterChain_WithRealFilters_ShouldWorkCorrectly() {
        var filterService = new FilterServiceImpl();
        var recipes = new ArrayList<RecipeDto>();
        var searchContainer = Instancio.create(SearchContainer.class);
        searchContainer.maxMinsForCooking(0);
        searchContainer.maxMinsForCooking(Integer.MAX_VALUE);
        searchContainer.maxTotalMinutes(Integer.MAX_VALUE);
        searchContainer.minServings(0);
        searchContainer.maxServings(Integer.MAX_VALUE);
        recipes.add(createRecipe(30, 15, 45, 4));
        recipes.add(createRecipe(20, 10, 30, 2));

        List<RecipeDto> result = filterService.processWithFilterChain(recipes, searchContainer);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void filterChain_ShouldContainCorrectFilters() throws Exception {
        var filterService = new FilterServiceImpl();
        var filterChainField = FilterServiceImpl.class.getDeclaredField("filterChain");
        filterChainField.setAccessible(true);
        List<Filter> filterChain = (List<Filter>) filterChainField.get(filterService);

        assertNotNull(filterChain);
        assertEquals(4, filterChain.size());
        assertInstanceOf(Mins4CookFilter.class, filterChain.get(0));
        assertInstanceOf(Mins4PrepFilter.class, filterChain.get(1));
        assertInstanceOf(TotalMinsFilter.class, filterChain.get(2));
        assertInstanceOf(ServingsFilter.class, filterChain.get(3));
    }

    private RecipeDto createRecipe(int cookMins, int prepMins, int totalMins, int servings) {
        RecipeDto recipe = mock(RecipeDto.class);
        when(recipe.minsForCooking()).thenReturn(Duration.ofMinutes(cookMins));
        when(recipe.minsForPreparing()).thenReturn(Duration.ofMinutes(prepMins));
        when(recipe.totalMins()).thenReturn(Duration.ofMinutes(totalMins));
        when(recipe.servings()).thenReturn(servings);
        return recipe;
    }
}
