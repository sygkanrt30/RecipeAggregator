package ru.practice.recipe_aggregator.recipe_management.search_service.search;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practice.recipe_aggregator.recipe_management.model.dto.container.SearchContainer;
import ru.practice.recipe_aggregator.recipe_management.model.dto.mapper.RecipeMapper;
import ru.practice.recipe_aggregator.recipe_management.model.entity.elasticsearch.RecipeDoc;
import ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering.FilterService;
import ru.practice.recipe_aggregator.recipe_management.search_service.search.searcher.IngredientsSearcher;
import ru.practice.recipe_aggregator.recipe_management.search_service.search.searcher.NameSearcher;
import ru.practice.shared.dto.RecipeDto;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceImplTest {

    @Mock
    private FilterService filterService;
    @Mock
    private RecipeMapper mapper;
    @Mock
    private NameSearcher nameSearcher;
    @Mock
    private IngredientsSearcher ingredientsSearcher;
    @InjectMocks
    private SearchServiceImpl searchService;

    @Test
    void searchByNameWithFiltering_WhenNoResults_ShouldReturnEmptyListWithoutFiltering() {
        // Given
        var container = SearchContainer.builder().name("nonexistent").build();
        when(nameSearcher.search("nonexistent")).thenReturn(List.of());

        // When
        var result = searchService.searchByNameWithFiltering(container);

        // Then
        assertTrue(result.isEmpty());
        verify(nameSearcher).search("nonexistent");
        verifyNoInteractions(filterService);
    }

    @Test
    void searchByNameWithFiltering_WhenHasResults_ShouldApplyFiltering() {
        // Given
        var container = SearchContainer.builder().name("pasta").build();
        var recipeDoc = mock(RecipeDoc.class);
        var recipeDto = mock(RecipeDto.class);

        when(nameSearcher.search("pasta")).thenReturn(List.of(recipeDoc));
        when(mapper.toRecipeDto(recipeDoc)).thenReturn(recipeDto);

        // When
        var result = searchService.searchByNameWithFiltering(container);

        // Then
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(recipeDto, result.getFirst());
    }

    @Test
    void searchByIngredientsWithFiltering_WhenNoResults_ShouldReturnEmptyListWithoutFiltering() {
        // Given
        var ingredients = Set.of("nonexistent");
        var container = SearchContainer.builder().ingredientNames(ingredients).build();
        when(ingredientsSearcher.search(ingredients)).thenReturn(List.of());

        // When
        var result = searchService.searchByIngredientsWithFiltering(container);

        // Then
        assertTrue(result.isEmpty());
        verify(ingredientsSearcher).search(ingredients);
        verifyNoInteractions(filterService);
    }

    @Test
    void searchByIngredientsWithFiltering_WhenHasResults_ShouldApplyFiltering() {
        // Given
        var ingredients = Set.of("tomato", "cheese");
        var container = SearchContainer.builder().ingredientNames(ingredients).build();
        var recipeDoc = mock(RecipeDoc.class);
        var recipeDto = mock(RecipeDto.class);

        when(ingredientsSearcher.search(ingredients)).thenReturn(List.of(recipeDoc));
        when(mapper.toRecipeDto(recipeDoc)).thenReturn(recipeDto);

        // When
        var result = searchService.searchByIngredientsWithFiltering(container);

        // Then
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(recipeDto, result.getFirst());
    }

    @Test
    void searchByName_ShouldReturnMappedResults() {
        // Given
        String name = "pizza";
        var recipeDoc1 = mock(RecipeDoc.class);
        var recipeDoc2 = mock(RecipeDoc.class);
        var recipeDto1 = mock(RecipeDto.class);
        var recipeDto2 = mock(RecipeDto.class);

        when(nameSearcher.search(name)).thenReturn(List.of(recipeDoc1, recipeDoc2));
        when(mapper.toRecipeDto(recipeDoc1)).thenReturn(recipeDto1);
        when(mapper.toRecipeDto(recipeDoc2)).thenReturn(recipeDto2);

        // When
        var result = searchService.searchByName(name);

        // Then
        assertEquals(2, result.size());
        assertTrue(result.contains(recipeDto1));
        assertTrue(result.contains(recipeDto2));
    }

    @Test
    void searchByIngredients_ShouldReturnMappedResults() {
        // Given
        var ingredients = Set.of("flour", "sugar");
        var recipeDoc1 = mock(RecipeDoc.class);
        var recipeDoc2 = mock(RecipeDoc.class);
        var recipeDto1 = mock(RecipeDto.class);
        var recipeDto2 = mock(RecipeDto.class);

        when(ingredientsSearcher.search(ingredients)).thenReturn(List.of(recipeDoc1, recipeDoc2));
        when(mapper.toRecipeDto(recipeDoc1)).thenReturn(recipeDto1);
        when(mapper.toRecipeDto(recipeDoc2)).thenReturn(recipeDto2);

        // When
        var result = searchService.searchByIngredients(ingredients);

        // Then
        assertEquals(2, result.size());
        assertTrue(result.contains(recipeDto1));
        assertTrue(result.contains(recipeDto2));
    }

    @Test
    void searchByName_WithEmptyResult_ShouldReturnEmptyList() {
        // Given
        String name = "nonexistent";
        when(nameSearcher.search(name)).thenReturn(List.of());

        // When
        var result = searchService.searchByName(name);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void searchByIngredients_WithEmptyResult_ShouldReturnEmptyList() {
        // Given
        var ingredients = Set.of("nonexistent");
        when(ingredientsSearcher.search(ingredients)).thenReturn(List.of());

        // When
        var result = searchService.searchByIngredients(ingredients);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void searchByNameWithFiltering_ShouldUseCorrectNameFromContainer() {
        // Given
        var container = SearchContainer.builder().name("specific name").build();
        when(nameSearcher.search("specific name")).thenReturn(List.of());

        // When
        var result = searchService.searchByNameWithFiltering(container);

        // Then
        assertTrue(result.isEmpty());
        verify(nameSearcher).search("specific name");
    }

    @Test
    void searchByIngredientsWithFiltering_ShouldUseCorrectIngredientsFromContainer() {
        // Given
        var ingredients = Set.of("specific", "ingredients");
        var container = SearchContainer.builder().ingredientNames(ingredients).build();
        when(ingredientsSearcher.search(ingredients)).thenReturn(List.of());

        // When
        var result = searchService.searchByIngredientsWithFiltering(container);

        // Then
        assertTrue(result.isEmpty());
        verify(ingredientsSearcher).search(ingredients);
    }
}