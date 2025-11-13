package ru.practice.recipe_aggregator.recipe_management.search_service.search;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practice.recipe_aggregator.recipe_management.model.dto.container.SearchContainer;
import ru.practice.recipe_aggregator.recipe_management.model.dto.mapper.RecipeMapper;
import ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering.FilterService;
import ru.practice.recipe_aggregator.recipe_management.search_service.search.searcher.NameSearcher;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SearchServiceImplTest {

    @Mock
    private FilterService filterService;
    @Mock
    private RecipeMapper mapper;
    @Mock
    private NameSearcher nameSearcher;
    @InjectMocks
    private SearchServiceImpl searchService;

    @Test
    void searchByName_WhenNameIsNull_ShouldThrowException() {
        var container = SearchContainer.builder().name(null).build();
        var exception = assertThrows(IllegalArgumentException.class,
                () -> searchService.searchByName(null));
        assertEquals("Search name cannot be empty or null", exception.getMessage());
    }

    @Test
    void searchByName_WhenNameIsEmpty_ShouldThrowException() {
        var container = SearchContainer.builder().name("").build();
        var exception = assertThrows(IllegalArgumentException.class,
                () -> searchService.searchByName(""));
        assertEquals("Search name cannot be empty or null", exception.getMessage());
    }

    @Test
    void searchByIngredients_WhenIngredientsIsNull_ShouldThrowException() {
        var container = SearchContainer.builder().ingredientNames(null).build();
        var exception = assertThrows(IllegalArgumentException.class,
                () -> searchService.searchByIngredients(null));
        assertEquals("Ingredients name cannot be empty or null", exception.getMessage());
    }

    @Test
    void searchByIngredients_WhenIngredientsIsEmpty_ShouldThrowException() {
        var container = SearchContainer.builder().ingredientNames(Set.of()).build();
        var exception = assertThrows(IllegalArgumentException.class,
                () -> searchService.searchByIngredients(Set.of()));
        assertEquals("Ingredients name cannot be empty or null", exception.getMessage());
    }

    @Test
    void searchByNameWithFiltering_WhenNoResults_ShouldReturnEmptyListWithoutFiltering() {
        var container = SearchContainer.builder().name("nonexistent").build();

        var result = searchService.searchByNameWithFiltering(container);

        assertTrue(result.isEmpty());
    }

    @Test
    void searchByNameWithFiltering_WhenNameIsInvalid_ShouldThrowException() {
        var container = SearchContainer.builder().name("").build();

        assertThrows(IllegalArgumentException.class,
                () -> searchService.searchByNameWithFiltering(container));
        verify(filterService, never()).processWithFilterChain(anyList(), any());
    }

    @Test
    void searchByIngredientsWithFiltering_WhenIngredientsIsInvalid_ShouldThrowException() {
        var container = SearchContainer.builder().ingredientNames(null).build();

        assertThrows(IllegalArgumentException.class,
                () -> searchService.searchByIngredientsWithFiltering(container));
        verify(filterService, never()).processWithFilterChain(anyList(), any());
    }
}