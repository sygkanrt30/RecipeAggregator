package ru.practice.search_service.service.search;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.practice.search_service.model.dto.container.SearchContainer;
import ru.practice.search_service.model.dto.mapper.DtoMapper;
import ru.practice.search_service.service.filtering.FilterService;
import ru.practice.search_service.service.search.searcher.Searcher;

import java.util.List;

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
    private DtoMapper mapper;

    @Mock
    @Qualifier("nameSearcher")
    private Searcher nameSearcher;

    @Mock
    @Qualifier("ingredientsSearcher")
    private Searcher ingredientsSearcher;

    @InjectMocks
    private SearchServiceImpl searchService;

    @Test
    void searchByName_WhenNameIsNull_ShouldThrowException() {
        // Arrange
        var container = SearchContainer.builder().name(null).build();

        // Act & Assert
        var exception = assertThrows(IllegalArgumentException.class,
                () -> searchService.searchByName(container));
        assertEquals("Search name cannot be empty or null", exception.getMessage());
    }

    @Test
    void searchByName_WhenNameIsEmpty_ShouldThrowException() {
        // Arrange
        var container = SearchContainer.builder().name("").build();

        // Act & Assert
        var exception = assertThrows(IllegalArgumentException.class,
                () -> searchService.searchByName(container));
        assertEquals("Search name cannot be empty or null", exception.getMessage());
    }

    @Test
    void searchByIngredients_WhenIngredientsIsNull_ShouldThrowException() {
        // Arrange
        var container = SearchContainer.builder().ingredientsName(null).build();

        // Act & Assert
        var exception = assertThrows(IllegalArgumentException.class,
                () -> searchService.searchByIngredients(container));
        assertEquals("Ingredients name cannot be empty or null", exception.getMessage());
    }

    @Test
    void searchByIngredients_WhenIngredientsIsEmpty_ShouldThrowException() {
        // Arrange
        var container = SearchContainer.builder().ingredientsName(List.of()).build();

        // Act & Assert
        var exception = assertThrows(IllegalArgumentException.class,
                () -> searchService.searchByIngredients(container));
        assertEquals("Ingredients name cannot be empty or null", exception.getMessage());
    }

    @Test
    void searchByIngredientsWithFiltering_WhenNoResults_ShouldReturnEmptyListWithoutFiltering() {
        // Arrange
        var ingredients = List.of("chicken", "rice");
        var container = SearchContainer.builder().ingredientsName(ingredients).build();

        // Act
        var result = searchService.searchByIngredientsWithFiltering(container);

        // Assert
        assertTrue(result.isEmpty());
        verify(filterService, never()).processWithFilterChain(anyList(), any());
    }

    @Test
    void searchByName_WhenSearcherReturnsEmptyList_ShouldReturnEmptyList() {
        // Arrange
        var container = SearchContainer.builder().name("test").build();

        // Act
        var result = searchService.searchByName(container);

        // Assert
        assertTrue(result.isEmpty());
        verify(mapper, never()).toRecipeResponseDto(any());
    }

    @Test
    void searchByIngredients_WhenSearcherReturnsEmptyList_ShouldReturnEmptyList() {
        // Arrange
        var ingredients = List.of("test");
        var container = SearchContainer.builder().ingredientsName(ingredients).build();

        // Act
        var result = searchService.searchByIngredients(container);

        // Assert
        assertTrue(result.isEmpty());
        verify(mapper, never()).toRecipeResponseDto(any());
    }

    @Test
    void searchByNameWithFiltering_ShouldNotCallFilterServiceWhenResultsEmpty() {
        // Arrange
        var container = SearchContainer.builder().name("nonexistent").build();

        // Act
        var result = searchService.searchByNameWithFiltering(container);

        // Assert
        assertTrue(result.isEmpty());
        verify(filterService, never()).processWithFilterChain(anyList(), any());
    }
}